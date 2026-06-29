package com.nexa.platform.sales.application.internal;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.domain.model.repositories.ProductRepositoryPort;
import com.nexa.platform.invoicing.application.dtos.BusinessDocumentDtos;
import com.nexa.platform.invoicing.application.dtos.PaymentProcessRecordDtos;
import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.logistics.application.dtos.OperationalRecordRequests;
import com.nexa.platform.logistics.application.dtos.UpsertDispatchOrderRequest;
import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.domain.model.repositories.DispatchOrderRepositoryPort;
import com.nexa.platform.sales.application.dtos.PurchaseRequestWorkflowDtos;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.domain.model.repositories.*;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.warehouse.application.dtos.InventoryOperationDtos;
import com.nexa.platform.warehouse.application.internal.WarehouseService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseRequestWorkflowService {
    private final PurchaseRequestRepositoryPort purchaseRequests;
    private final SalesOrderRepositoryPort orders;
    private final CustomerRepositoryPort customers;
    private final ProductRepositoryPort products;
    private final ConversationMessageRepositoryPort messages;
    private final DispatchOrderRepositoryPort dispatchOrders;
    private final WarehouseService warehouse;
    private final LogisticsService logistics;
    private final InvoicingService invoicing;

    public PurchaseRequestWorkflowService(PurchaseRequestRepositoryPort purchaseRequests,
                                          SalesOrderRepositoryPort orders,
                                          CustomerRepositoryPort customers,
                                          ProductRepositoryPort products,
                                          ConversationMessageRepositoryPort messages,
                                          DispatchOrderRepositoryPort dispatchOrders,
                                          WarehouseService warehouse,
                                          LogisticsService logistics,
                                          InvoicingService invoicing) {
        this.purchaseRequests = purchaseRequests;
        this.orders = orders;
        this.customers = customers;
        this.products = products;
        this.messages = messages;
        this.dispatchOrders = dispatchOrders;
        this.warehouse = warehouse;
        this.logistics = logistics;
        this.invoicing = invoicing;
    }

    @Transactional
    public PurchaseRequestWorkflowDtos.OrderAcceptanceResponse accept(
        Long tenantId, String requestReference, String note) {
        PurchaseRequest request = findRequest(requestReference);
        requireTenant(request, tenantId);
        if ("converted_to_order".equals(request.getStatus()) && request.getAcceptedOrderId() != null) {
            Long dispatchId = dispatchOrders.findByOrderId(request.getAcceptedOrderId())
                .map(dispatch -> dispatch.getId())
                .orElse(null);
            return new PurchaseRequestWorkflowDtos.OrderAcceptanceResponse(
                request.getId(), request.getAcceptedOrderId(), dispatchId, "already_accepted");
        }
        if (!"commercially_validated".equals(request.getStatus())) {
            throw new IllegalStateException("Only commercially validated purchase requests can be accepted.");
        }
        if (request.getItems().isEmpty()) throw new IllegalStateException("Purchase request has no lines.");

        Customer customer = resolveCustomer(tenantId, request.getClientId());
        List<SalesOrderItem> orderItems = new ArrayList<>();
        for (PurchaseRequestLine line : request.getItems()) {
            Product product = line.getProduct();
            product.reserveStock(line.getQuantity());
            products.save(product);
            orderItems.add(new SalesOrderItem(product, line.getQuantity(), product.getUnitPrice()));
        }

        SalesOrder order = new SalesOrder(tenantId, customer);
        order.update(customer, orderItems, request.getPriority(), note == null || note.isBlank() ? request.getComments() : note);
        order = orders.save(order);
        String orderNumber = orderNumber(order.getId());

        int lineNumber = 1;
        for (PurchaseRequestLine line : request.getItems()) {
            warehouse.createReservation(tenantId, new InventoryOperationDtos.CreateInventoryReservationRequest(
                "RES-PR-" + request.getId() + "-" + String.format("%02d", lineNumber++),
                null, line.getProduct().getSku(), null, orderNumber, request.getId(), line.getQuantity()));
        }

        for (String type : List.of("factura_xml", "factura_pdf", "guia_pdf")) {
            invoicing.generateBusinessDocument(tenantId,
                new BusinessDocumentDtos.GenerateBusinessDocumentRequest(tenantId, order.getId(), type));
        }
        BigDecimal subtotal = order.total();
        BigDecimal shipping = request.getShippingEstimate() == null ? BigDecimal.ZERO : request.getShippingEstimate();
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        invoicing.createPaymentProcess(tenantId, new PaymentProcessRecordDtos.CreatePaymentProcessRecordRequest(
            tenantId, order.getId(), customer.getId(), null, null, subtotal, BigDecimal.ZERO, shipping,
            igv, subtotal.add(shipping).add(igv).setScale(2, RoundingMode.HALF_UP), "pending"));

        var dispatch = logistics.createDispatchOrder(new UpsertDispatchOrderRequest(
            tenantId, order.getId(), customer.getId(), "DSP-" + orderNumber, "ready_for_operations",
            routeName(request), null, null, ""));
        logistics.createDispatchEvent(new OperationalRecordRequests.UpsertDispatchEventRequest(
            tenantId, dispatch.id(), "ready_for_operations",
            "Order " + orderNumber + " is ready for logistics operations.", true));
        logistics.createProofOfDelivery(new OperationalRecordRequests.UpsertProofOfDeliveryRecordRequest(
            tenantId, dispatch.id(), "", null, false, false,
            "Pending delivery confirmation.", "pending"));

        request.markAcceptedIntoOrder(order.getId(), orderNumber, note);
        purchaseRequests.save(request);
        messages.save(new ConversationMessage(tenantId, customer.getId(), request.getId(), order.getId(),
            "sales", request.getCommercialOwner().isBlank() ? "Sales" : request.getCommercialOwner(),
            "Sales accepted request " + request.getCode() + ", reserved live stock and created traceable order "
                + orderNumber + ". Logistics dispatch " + dispatch.id() + " is ready for operations.",
            true));

        return new PurchaseRequestWorkflowDtos.OrderAcceptanceResponse(
            request.getId(), order.getId(), dispatch.id(), "accepted");
    }

    @Transactional
    public PurchaseRequestWorkflowDtos.ReservationResponse reserve(
        Long tenantId, String requestReference, PurchaseRequestWorkflowDtos.ReservationRequest draft) {
        PurchaseRequest request = findRequest(requestReference);
        requireTenant(request, tenantId);
        if (!List.of("commercially_validated", "converted_to_order").contains(request.getStatus())) {
            throw new IllegalStateException("Only commercially validated purchase requests can reserve stock.");
        }
        var reservation = warehouse.createReservation(tenantId,
            new InventoryOperationDtos.CreateInventoryReservationRequest(
                draft.code(), draft.inventoryItemId(), draft.productId(), draft.lotCode(),
                null, request.getId(), draft.units()));
        return new PurchaseRequestWorkflowDtos.ReservationResponse(
            reservation.id(), reservation.code(), reservation.status());
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestWorkflowDtos.MessageResponse> listMessages(Long tenantId) {
        requireTenantId(tenantId);
        return messages.findByTenantIdOrderByIdAsc(tenantId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestWorkflowDtos.MessageResponse> listMessages(Long tenantId, Long clientAccountId) {
        requireTenantId(tenantId);
        return messages.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(message -> clientAccountId == null || (clientAccountId.equals(message.getClientAccountId())
                && message.isVisibleToBuyer()))
            .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PurchaseRequestWorkflowDtos.MessageResponse getMessage(Long tenantId, Long id) {
        return toResponse(findMessage(tenantId, id));
    }

    @Transactional(readOnly = true)
    public PurchaseRequestWorkflowDtos.MessageResponse getMessage(Long tenantId, Long clientAccountId, Long id) {
        ConversationMessage message = findMessage(tenantId, id);
        if (clientAccountId != null && (!clientAccountId.equals(message.getClientAccountId())
            || !message.isVisibleToBuyer())) {
            throw new ResourceNotFoundException("Conversation message", id);
        }
        return toResponse(message);
    }

    @Transactional
    public PurchaseRequestWorkflowDtos.MessageResponse createMessage(
        Long tenantId, PurchaseRequestWorkflowDtos.MessageRequest draft) {
        requireTenantId(tenantId);
        validateMessageReferences(tenantId, draft.purchaseRequestId(), draft.orderId());
        ConversationMessage message = new ConversationMessage(
            tenantId, draft.clientAccountId(), draft.purchaseRequestId(), draft.orderId(),
            draft.senderRole(), draft.senderName(), draft.body(), draft.visibleToBuyer());
        return toResponse(messages.save(message));
    }

    @Transactional
    public PurchaseRequestWorkflowDtos.MessageResponse createRequestMessage(
        Long tenantId, String requestReference, PurchaseRequestWorkflowDtos.MessageRequest draft) {
        PurchaseRequest request = findRequest(requestReference);
        requireTenant(request, tenantId);
        Customer customer = resolveCustomer(tenantId, request.getClientId());
        return createMessage(tenantId, new PurchaseRequestWorkflowDtos.MessageRequest(
            customer.getId(), request.getId(), request.getAcceptedOrderId(), draft.senderRole(),
            draft.senderName(), draft.body(), draft.visibleToBuyer()));
    }

    @Transactional
    public PurchaseRequestWorkflowDtos.MessageResponse updateMessage(
        Long tenantId, Long id, PurchaseRequestWorkflowDtos.MessageRequest draft) {
        ConversationMessage message = findMessage(tenantId, id);
        validateMessageReferences(tenantId, draft.purchaseRequestId(), draft.orderId());
        message.update(draft.clientAccountId(), draft.purchaseRequestId(), draft.orderId(), draft.senderRole(),
            draft.senderName(), draft.body(), draft.visibleToBuyer());
        return toResponse(messages.save(message));
    }

    @Transactional
    public void deleteMessage(Long tenantId, Long id) {
        messages.delete(findMessage(tenantId, id));
    }

    private void validateMessageReferences(Long tenantId, Long purchaseRequestId, Long orderId) {
        if (purchaseRequestId != null) {
            PurchaseRequest request = purchaseRequests.findById(purchaseRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase request", purchaseRequestId));
            requireTenant(request, tenantId);
        }
        if (orderId != null) {
            orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Sales order", orderId));
        }
    }

    private PurchaseRequest findRequest(String reference) {
        if (reference != null && reference.matches("\\d+")) {
            return purchaseRequests.findById(Long.valueOf(reference))
                .orElseThrow(() -> new ResourceNotFoundException("Purchase request", reference));
        }
        return purchaseRequests.findByCode(reference)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request", reference));
    }

    private Customer resolveCustomer(Long tenantId, String clientId) {
        if (clientId != null && clientId.matches("CLI-\\d+")) {
            return customers.findByIdAndTenantId(Long.valueOf(clientId.substring(4)), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", clientId));
        }
        throw new IllegalArgumentException("Purchase request client reference is invalid.");
    }

    private ConversationMessage findMessage(Long tenantId, Long id) {
        requireTenantId(tenantId);
        return messages.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation message", id));
    }

    private PurchaseRequestWorkflowDtos.MessageResponse toResponse(ConversationMessage message) {
        return new PurchaseRequestWorkflowDtos.MessageResponse(
            message.getId(), message.getTenantId(), message.getClientAccountId(), message.getPurchaseRequestId(),
            message.getOrderId(), message.getSenderRole(), message.getSenderName(), message.getBody(),
            message.isVisibleToBuyer(), message.getCreatedAt(), message.getUpdatedAt());
    }

    private static void requireTenant(PurchaseRequest request, Long tenantId) {
        requireTenantId(tenantId);
        if (!tenantId.equals(request.getTenantId())) {
            throw new IllegalArgumentException("Purchase request does not belong to the current tenant.");
        }
    }

    private static void requireTenantId(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
    }

    private static String orderNumber(Long orderId) {
        return "ORD-2026-" + String.format("%04d", orderId);
    }

    private static String routeName(PurchaseRequest request) {
        String route = request.getDeliveryAddress();
        if (request.getDeliveryDistrict() != null && !request.getDeliveryDistrict().isBlank()) {
            route += ", " + request.getDeliveryDistrict();
        }
        return route == null || route.isBlank() ? "Pending delivery route" : route.substring(0, Math.min(120, route.length()));
    }
}
