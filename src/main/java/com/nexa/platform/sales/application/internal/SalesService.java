package com.nexa.platform.sales.application.internal;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.domain.model.repositories.ProductRepositoryPort;
import com.nexa.platform.invoicing.domain.model.Invoice;
import com.nexa.platform.invoicing.domain.model.Payment;
import com.nexa.platform.invoicing.domain.model.repositories.InvoiceRepositoryPort;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentRepositoryPort;
import com.nexa.platform.logistics.domain.model.DispatchOrder;
import com.nexa.platform.logistics.domain.model.DispatchEvent;
import com.nexa.platform.logistics.domain.model.repositories.DispatchEventRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.DispatchOrderRepositoryPort;
import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.domain.model.repositories.*;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertPurchaseRequestItemResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertPurchaseRequestResource;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesService {
    private final CustomerRepositoryPort customers;
    private final SalesOrderRepositoryPort orders;
    private final PurchaseRequestRepositoryPort purchaseRequests;
    private final ProductRepositoryPort products;
    private final SalesMapper mapper;
    private final DispatchOrderRepositoryPort dispatchOrders;
    private final DispatchEventRepositoryPort dispatchEvents;
    private final InvoiceRepositoryPort invoices;
    private final PaymentRepositoryPort payments;

    public SalesService(CustomerRepositoryPort customers, SalesOrderRepositoryPort orders,
                        PurchaseRequestRepositoryPort purchaseRequests, ProductRepositoryPort products,
                        SalesMapper mapper, DispatchOrderRepositoryPort dispatchOrders,
                        DispatchEventRepositoryPort dispatchEvents, InvoiceRepositoryPort invoices,
                        PaymentRepositoryPort payments) {
        this.customers = customers;
        this.orders = orders;
        this.purchaseRequests = purchaseRequests;
        this.products = products;
        this.mapper = mapper;
        this.dispatchOrders = dispatchOrders;
        this.dispatchEvents = dispatchEvents;
        this.invoices = invoices;
        this.payments = payments;
    }
    @Transactional(readOnly = true)
    public List<CustomerResponse> listCustomers(Long tenantId, Long clientAccountId) {
        Long scopedTenantId = requireTenant(tenantId);
        if (clientAccountId != null) {
            return List.of(mapper.toCustomerResponse(findCustomer(scopedTenantId, clientAccountId)));
        }
        return customers.findByTenantIdOrderByBusinessNameAsc(scopedTenantId).stream()
            .map(mapper::toCustomerResponse).toList();
    }
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long tenantId, Long clientAccountId, Long id) {
        if (clientAccountId != null && !clientAccountId.equals(id)) {
            throw new ResourceNotFoundException("Client account", id);
        }
        return mapper.toCustomerResponse(findCustomer(requireTenant(tenantId), id));
    }
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByCode(Long tenantId, Long clientAccountId, String code) {
        Customer customer = customers.findByTenantIdAndCode(requireTenant(tenantId), code.trim().toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Client account", code));
        if (clientAccountId != null && !clientAccountId.equals(customer.getId())) {
            throw new ResourceNotFoundException("Client account", code);
        }
        return mapper.toCustomerResponse(customer);
    }
    @Transactional
    public CustomerResponse createCustomer(Long tenantId, UpsertCustomerRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        ensureCustomerIdentityAvailable(scopedTenantId, request.code(), request.taxId(), null);
        Customer customer = new Customer(scopedTenantId, request.code(), request.businessName(),
            request.commercialName(), request.taxId(), request.segment(), request.contact(), request.contactEmail(),
            request.phone(), request.deliveryAddress(), request.district(), request.province(),
            request.deliveryReference(), request.documentProfile(), request.paymentCondition(),
            request.monthlyCreditLimit(), request.monthlyCreditUsed(), request.monthlyCreditStatus(),
            request.deliveryPreference(), request.portalAccess(), request.sellerWorkspaceEmail(), request.status());
        return mapper.toCustomerResponse(customers.save(customer));
    }
    @Transactional
    public CustomerResponse updateCustomer(Long tenantId, Long clientAccountId, Long id, UpsertCustomerRequest request) {
        if (clientAccountId != null && !clientAccountId.equals(id)) {
            throw new ResourceNotFoundException("Client account", id);
        }
        Long scopedTenantId = requireTenant(tenantId);
        Customer customer = findCustomer(scopedTenantId, id);
        ensureCustomerIdentityAvailable(scopedTenantId, customer.getCode(), request.taxId(), id);
        customer.update(request.businessName(), request.commercialName(), request.taxId(), request.segment(),
            request.contact(), request.contactEmail(), request.phone(), request.deliveryAddress(), request.district(),
            request.province(), request.deliveryReference(), request.documentProfile(), request.paymentCondition(),
            request.monthlyCreditLimit(), request.monthlyCreditUsed(), request.monthlyCreditStatus(),
            request.deliveryPreference(), request.portalAccess(), request.sellerWorkspaceEmail(), request.status());
        return mapper.toCustomerResponse(customers.save(customer));
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Long tenantId) {
        return orders.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream()
            .map(mapper::toOrderResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Long tenantId, Long clientAccountId) {
        Long scopedTenantId = requireTenant(tenantId);
        return orders.findByTenantIdOrderByIdAsc(scopedTenantId).stream()
            .filter(order -> clientAccountId == null || clientAccountId.equals(order.getCustomer().getId()))
            .map(mapper::toOrderResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long tenantId, Long id) { return mapper.toOrderResponse(findOrderWithItems(requireTenant(tenantId), id)); }
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long tenantId, String id) { return getOrder(tenantId, orderId(id)); }
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long tenantId, Long clientAccountId, String id) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), orderId(id));
        requireOrderClient(order, clientAccountId);
        return mapper.toOrderResponse(order);
    }
    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> listPurchaseRequests(Long tenantId) {
        return purchaseRequests.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream()
            .map(mapper::toPurchaseRequestResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> listPurchaseRequests(Long tenantId, Long clientAccountId) {
        Long scopedTenantId = requireTenant(tenantId);
        String clientCode = clientCode(scopedTenantId, clientAccountId);
        return purchaseRequests.findByTenantIdOrderByIdAsc(scopedTenantId).stream()
            .filter(request -> clientCode == null || clientCode.equalsIgnoreCase(request.getClientId()))
            .map(mapper::toPurchaseRequestResponse).toList();
    }
    @Transactional(readOnly = true)
    public PurchaseRequestResponse getPurchaseRequest(Long tenantId, String id) {
        return mapper.toPurchaseRequestResponse(findPurchaseRequest(requireTenant(tenantId), id));
    }
    @Transactional(readOnly = true)
    public PurchaseRequestResponse getPurchaseRequest(Long tenantId, Long clientAccountId, String id) {
        Long scopedTenantId = requireTenant(tenantId);
        PurchaseRequest request = findPurchaseRequest(scopedTenantId, id);
        String clientCode = clientCode(scopedTenantId, clientAccountId);
        if (clientCode != null && !clientCode.equalsIgnoreCase(request.getClientId())) {
            throw new ResourceNotFoundException("Purchase request", id);
        }
        return mapper.toPurchaseRequestResponse(request);
    }
    @Transactional
    public PurchaseRequestResponse createPurchaseRequest(Long tenantId, Long authenticatedClientAccountId,
                                                         UpsertPurchaseRequestResource request) {
        Long scopedTenantId = requireTenant(tenantId);
        Customer customer = purchaseRequestCustomer(scopedTenantId, authenticatedClientAccountId, request);
        PurchaseRequest purchaseRequest = new PurchaseRequest(scopedTenantId, customer.getCode(),
            codeOrNext(scopedTenantId, request.code()), request.origin(), request.status(), request.priority(),
            request.requestedDeliveryDate(), request.deliveryAddress(), request.deliveryDistrict(),
            request.deliveryCity(), request.deliveryProvince(), request.deliveryReference(), request.paymentOption(),
            request.shippingEstimate(), request.comments(), request.commercialOwner());
        purchaseRequest.replaceItems(toPurchaseRequestLines(scopedTenantId, request.items()));
        return mapper.toPurchaseRequestResponse(purchaseRequests.save(purchaseRequest));
    }
    @Transactional
    public PurchaseRequestResponse updatePurchaseRequest(Long tenantId, String id,
                                                         UpsertPurchaseRequestResource request) {
        return updatePurchaseRequest(tenantId, null, id, request);
    }
    @Transactional
    public PurchaseRequestResponse updatePurchaseRequest(Long tenantId, Long authenticatedClientAccountId, String id,
                                                         UpsertPurchaseRequestResource request) {
        Long scopedTenantId = requireTenant(tenantId);
        PurchaseRequest purchaseRequest = findPurchaseRequest(scopedTenantId, id);
        Customer customer = purchaseRequestCustomer(scopedTenantId, authenticatedClientAccountId, request);
        purchaseRequest.update(customer.getCode(), codeOrCurrent(scopedTenantId, request.code(), purchaseRequest.getCode()),
            request.origin(), request.status(), request.priority(), request.requestedDeliveryDate(), request.deliveryAddress(),
            request.deliveryDistrict(), request.deliveryCity(), request.deliveryProvince(), request.deliveryReference(),
            request.paymentOption(), request.shippingEstimate(), request.comments(), request.commercialOwner());
        if (request.items() != null) {
            purchaseRequest.replaceItems(toPurchaseRequestLines(scopedTenantId, request.items()));
        }
        return mapper.toPurchaseRequestResponse(purchaseRequests.save(purchaseRequest));
    }
    @Transactional
    public void deletePurchaseRequest(Long tenantId, String id) {
        purchaseRequests.delete(findPurchaseRequest(requireTenant(tenantId), id));
    }
    @Transactional
    public PurchaseRequestResponse submitPurchaseRequest(Long tenantId, String id, String note) {
        return changePurchaseRequestStatus(requireTenant(tenantId), id, "submitted", note, null);
    }
    @Transactional
    public PurchaseRequestResponse requestPurchaseRequestAdjustment(Long tenantId, String id, String note) {
        return changePurchaseRequestStatus(requireTenant(tenantId), id, "buyer_adjustment_requested", note, null);
    }
    @Transactional
    public PurchaseRequestResponse rejectPurchaseRequest(Long tenantId, String id, String note) {
        return changePurchaseRequestStatus(requireTenant(tenantId), id, "rejected", note, null);
    }
    @Transactional
    public PurchaseRequestResponse validatePurchaseRequestCommercially(Long tenantId, String id, String commercialOwner, String comments) {
        return changePurchaseRequestStatus(requireTenant(tenantId), id, "commercially_validated", comments, commercialOwner);
    }
    @Transactional
    public PurchaseRequestResponse cancelPurchaseRequest(Long tenantId, String id, String note) {
        return changePurchaseRequestStatus(requireTenant(tenantId), id, "cancelled", note, null);
    }
    @Transactional
    public OrderResponse createOrder(Long tenantId, CreateOrderRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        Customer customer = findCustomer(scopedTenantId, request.customerId());
        SalesOrder order = new SalesOrder(scopedTenantId, customer);
        request.items().stream().map(this::toOrderItem).forEach(order::addItem);
        SalesOrder saved = orders.save(order);
        ensureDispatchForOrder(saved, "Sales order " + orderNumber(saved) + " is ready for logistics operations.");
        return mapper.toOrderResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrder(Long tenantId, Long id, UpdateOrderRequest request) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), id);
        Customer customer = findCustomer(requireTenant(tenantId), request.customerId());
        List<SalesOrderItem> items = request.items().stream().map(this::toOrderItem).toList();
        order.update(customer, items, request.priority(), request.notes());
        return mapper.toOrderResponse(orders.save(order));
    }

    @Transactional
    public OrderResponse confirmOrder(Long tenantId, Long id, String paymentConfirmation, String inventoryReservation) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), id);
        order.confirm(paymentConfirmation, inventoryReservation);
        SalesOrder saved = orders.save(order);
        ensureDispatchForOrder(saved, "Sales order " + orderNumber(saved) + " was confirmed and remains ready for logistics operations.");
        return mapper.toOrderResponse(saved);
    }

    @Transactional
    public OrderResponse rejectOrder(Long tenantId, Long id, String rejectionReason) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), id);
        order.reject(rejectionReason);
        return mapper.toOrderResponse(orders.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long tenantId, Long id) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), id);
        order.cancel();
        return mapper.toOrderResponse(orders.save(order));
    }

    @Transactional(readOnly = true)
    public OrderTimelineResponse getOrderTimeline(Long tenantId, Long id) {
        Long scopedTenantId = requireTenant(tenantId);
        SalesOrder order = findOrderWithItems(scopedTenantId, id);
        List<OrderTimelineResponse.TimelineEventResponse> timeline = new java.util.ArrayList<>();
        timeline.add(new OrderTimelineResponse.TimelineEventResponse(
            "order", order.getStatus().name().toLowerCase(),
            "Order " + orderNumber(order) + " created.", order.getCreatedAt()));

        dispatchOrders.findByTenantIdAndOrderId(scopedTenantId, id).ifPresent(dispatch -> dispatchEvents
            .findByDispatchOrderIdOrderByIdAsc(dispatch.getId())
            .forEach(event -> timeline.add(new OrderTimelineResponse.TimelineEventResponse(
                "dispatch", event.getStatus(), event.getDescription(), event.getCreatedAt()))));

        for (Invoice invoice : invoices.findByTenantIdAndOrderIdOrderByIdAsc(scopedTenantId, id)) {
            timeline.add(new OrderTimelineResponse.TimelineEventResponse(
                "invoice", invoice.getStatus().name().toLowerCase(),
                "Invoice " + invoice.getInvoiceNumber() + ".", invoice.getCreatedAt()));
        }
        for (Payment payment : payments.findByTenantIdAndInvoiceOrderIdOrderByIdAsc(scopedTenantId, id)) {
            timeline.add(new OrderTimelineResponse.TimelineEventResponse(
                "payment", "recorded", "Payment " + payment.getId() + ".",
                payment.getCreatedAt()));
        }
        timeline.sort(Comparator.comparing(OrderTimelineResponse.TimelineEventResponse::occurredAt));
        return new OrderTimelineResponse(order.getId(), orderNumber(order), timeline);
    }

    @Transactional(readOnly = true)
    public OrderTimelineResponse getOrderTimeline(Long tenantId, Long clientAccountId, Long id) {
        Long scopedTenantId = requireTenant(tenantId);
        requireOrderClient(findOrderWithItems(scopedTenantId, id), clientAccountId);
        return getOrderTimeline(scopedTenantId, id);
    }

    @Transactional
    public OrderResponse updateStatus(Long tenantId, Long id, UpdateOrderStatusRequest request) {
        SalesOrder order = findOrderWithItems(requireTenant(tenantId), id);
        order.changeStatus(OrderStatus.valueOf(request.status()));
        return mapper.toOrderResponse(orders.save(order));
    }

    private PurchaseRequestResponse changePurchaseRequestStatus(Long tenantId, String id, String status, String comments, String commercialOwner) {
        PurchaseRequest request = findPurchaseRequest(tenantId, id);
        request.changeStatus(status, comments, commercialOwner);
        return mapper.toPurchaseRequestResponse(purchaseRequests.save(request));
    }

    private PurchaseRequest findPurchaseRequest(Long tenantId, String id) {
        if (id != null && id.matches("\\d+")) {
            return purchaseRequests.findByIdAndTenantId(Long.valueOf(id), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase request", id));
        }
        return purchaseRequests.findByTenantIdAndCode(tenantId, id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request", id));
    }

    private Customer purchaseRequestCustomer(Long tenantId, Long authenticatedClientAccountId,
                                             UpsertPurchaseRequestResource request) {
        if (authenticatedClientAccountId != null) {
            if (request.clientAccountId() != null && !authenticatedClientAccountId.equals(request.clientAccountId())) {
                throw new ResourceNotFoundException("Client account", request.clientAccountId());
            }
            return findCustomer(tenantId, authenticatedClientAccountId);
        }
        if (request.clientAccountId() != null) return findCustomer(tenantId, request.clientAccountId());
        if (request.clientId() != null && !request.clientId().isBlank()) {
            return customers.findByTenantIdAndCode(tenantId, request.clientId().trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Client account", request.clientId()));
        }
        throw new IllegalArgumentException("Client account is required.");
    }

    private List<PurchaseRequestLine> toPurchaseRequestLines(Long tenantId, List<UpsertPurchaseRequestItemResource> items) {
        if (items == null) return List.of();
        return items.stream().map(item -> {
            Product product = purchaseRequestProduct(item);
            return new PurchaseRequestLine(tenantId, product, normalizedQuantity(item.quantity()),
                item.unit(), nonNegative(item.estimatedWeightKg(), "Estimated weight cannot be negative."), item.notes());
        }).toList();
    }

    private Product purchaseRequestProduct(UpsertPurchaseRequestItemResource item) {
        if (item.catalogItemId() != null) {
            return products.findById(item.catalogItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item", item.catalogItemId()));
        }
        if (item.productId() != null && !item.productId().isBlank()) {
            String productId = item.productId().trim();
            if (productId.matches("\\d+")) {
                return products.findById(Long.valueOf(productId))
                    .orElseThrow(() -> new ResourceNotFoundException("Catalog item", productId));
            }
            return products.findBySku(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item", productId));
        }
        throw new IllegalArgumentException("Catalog item is required.");
    }

    private String codeOrNext(Long tenantId, String code) {
        if (code != null && !code.isBlank()) return code.trim().toUpperCase();
        int next = purchaseRequests.findByTenantIdOrderByIdAsc(tenantId).size() + 1;
        String candidate;
        do {
            candidate = "PR-2026-" + String.format("%04d", next++);
        } while (purchaseRequests.existsByCode(candidate));
        return candidate;
    }

    private String codeOrCurrent(Long tenantId, String code, String current) {
        if (code == null || code.isBlank()) return current;
        String next = code.trim().toUpperCase();
        if (!next.equals(current) && purchaseRequests.existsByCode(next)) {
            throw new IllegalArgumentException("Purchase request code already exists.");
        }
        return next;
    }

    private static int normalizedQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        try {
            return quantity.intValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Quantity must be a whole number.");
        }
    }

    private static BigDecimal nonNegative(BigDecimal value, String message) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value;
        if (normalized.signum() < 0) throw new IllegalArgumentException(message);
        return normalized;
    }

    private Long orderId(String id) {
        if (id != null && id.matches("ORD-2026-\\d{4}")) {
            return Long.parseLong(id.substring(id.lastIndexOf('-') + 1));
        }
        return Long.valueOf(id);
    }

    private SalesOrder findOrderWithItems(Long tenantId, Long id) {
        return orders.findWithItemsByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Sales order", id));
    }

    private void requireOrderClient(SalesOrder order, Long clientAccountId) {
        if (clientAccountId != null && !clientAccountId.equals(order.getCustomer().getId())) {
            throw new ResourceNotFoundException("Sales order", order.getId());
        }
    }

    private SalesOrderItem toOrderItem(OrderItemRequest item) {
        Product product = products.findById(item.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));
        return new SalesOrderItem(product, item.quantity(), product.getUnitPrice());
    }

    private DispatchOrder ensureDispatchForOrder(SalesOrder order, String eventDescription) {
        return dispatchOrders.findByTenantIdAndOrderId(order.getTenantId(), order.getId())
            .orElseGet(() -> {
                DispatchOrder dispatch = dispatchOrders.save(new DispatchOrder(order.getTenantId(), order.getId(),
                    order.getCustomer().getId(), "DSP-" + orderNumber(order), "ready_for_operations",
                    routeName(order.getCustomer()), "", null, ""));
                dispatchEvents.save(new DispatchEvent(order.getTenantId(), dispatch.getId(), "ready_for_operations",
                    eventDescription, true));
                return dispatch;
            });
    }

    private static String routeName(Customer customer) {
        String route = customer.getDeliveryAddress() == null ? "" : customer.getDeliveryAddress();
        if (customer.getDistrict() != null && !customer.getDistrict().isBlank()) {
            route = route.isBlank() ? customer.getDistrict() : route + ", " + customer.getDistrict();
        }
        if (customer.getProvince() != null && !customer.getProvince().isBlank()) {
            route = route.isBlank() ? customer.getProvince() : route + ", " + customer.getProvince();
        }
        return route.isBlank() ? "Pending delivery route" : route.substring(0, Math.min(120, route.length()));
    }

    private String orderNumber(SalesOrder order) {
        return "ORD-2026-" + String.format("%04d", order.getId());
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        return tenantId;
    }

    private Customer findCustomer(Long tenantId, Long id) {
        return customers.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Client account", id));
    }

    private String clientCode(Long tenantId, Long clientAccountId) {
        return clientAccountId == null ? null : findCustomer(tenantId, clientAccountId).getCode();
    }

    private void ensureCustomerIdentityAvailable(Long tenantId, String code, String taxId, Long excludedId) {
        if (code != null && !code.isBlank()) {
            customers.findByTenantIdAndCode(tenantId, code.trim().toUpperCase())
                .filter(existing -> excludedId == null || !existing.getId().equals(excludedId))
                .ifPresent(existing -> { throw new IllegalArgumentException("Client code already exists."); });
        }
        customers.findByTenantIdAndTaxId(tenantId, taxId.trim())
            .filter(existing -> excludedId == null || !existing.getId().equals(excludedId))
            .ifPresent(existing -> { throw new IllegalArgumentException("Client tax id already exists."); });
    }
}
