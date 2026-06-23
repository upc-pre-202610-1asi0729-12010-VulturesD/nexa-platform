package com.nexa.platform.shared.application.readmodels;

import com.nexa.platform.invoicing.domain.model.BusinessDocument;
import com.nexa.platform.invoicing.domain.model.Invoice;
import com.nexa.platform.invoicing.domain.model.InvoiceStatus;
import com.nexa.platform.invoicing.domain.model.NotificationRecord;
import com.nexa.platform.invoicing.domain.model.Payment;
import com.nexa.platform.invoicing.domain.model.repositories.BusinessDocumentRepositoryPort;
import com.nexa.platform.invoicing.domain.model.repositories.InvoiceRepositoryPort;
import com.nexa.platform.invoicing.domain.model.repositories.NotificationRecordRepositoryPort;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentMethodRecordRepositoryPort;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentRepositoryPort;
import com.nexa.platform.logistics.domain.model.DispatchEvent;
import com.nexa.platform.logistics.domain.model.DispatchOrder;
import com.nexa.platform.logistics.domain.model.ProofOfDeliveryRecord;
import com.nexa.platform.logistics.domain.model.TemperatureLog;
import com.nexa.platform.logistics.domain.model.repositories.DispatchEventRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.DispatchOrderRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.ProofOfDeliveryRecordRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.TemperatureLogRepositoryPort;
import com.nexa.platform.sales.domain.model.ConversationMessage;
import com.nexa.platform.sales.domain.model.Customer;
import com.nexa.platform.sales.domain.model.OrderStatus;
import com.nexa.platform.sales.domain.model.PurchaseRequest;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.SalesOrderItem;
import com.nexa.platform.sales.domain.model.repositories.ConversationMessageRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.PurchaseRequestRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import com.nexa.platform.shared.application.pagination.PagedResult;
import com.nexa.platform.shared.application.security.WorkspaceScopeException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesBuyerReadModelService {
    private final SalesOrderRepositoryPort orders;
    private final PurchaseRequestRepositoryPort requests;
    private final CustomerRepositoryPort customers;
    private final ConversationMessageRepositoryPort messages;
    private final DispatchOrderRepositoryPort dispatches;
    private final DispatchEventRepositoryPort dispatchEvents;
    private final TemperatureLogRepositoryPort temperatures;
    private final ProofOfDeliveryRecordRepositoryPort proofs;
    private final BusinessDocumentRepositoryPort documents;
    private final InvoiceRepositoryPort invoices;
    private final PaymentRepositoryPort payments;
    private final PaymentMethodRecordRepositoryPort paymentMethods;
    private final NotificationRecordRepositoryPort notifications;

    public SalesBuyerReadModelService(
        SalesOrderRepositoryPort orders,
        PurchaseRequestRepositoryPort requests,
        CustomerRepositoryPort customers,
        ConversationMessageRepositoryPort messages,
        DispatchOrderRepositoryPort dispatches,
        DispatchEventRepositoryPort dispatchEvents,
        TemperatureLogRepositoryPort temperatures,
        ProofOfDeliveryRecordRepositoryPort proofs,
        BusinessDocumentRepositoryPort documents,
        InvoiceRepositoryPort invoices,
        PaymentRepositoryPort payments,
        PaymentMethodRecordRepositoryPort paymentMethods,
        NotificationRecordRepositoryPort notifications) {
        this.orders = orders;
        this.requests = requests;
        this.customers = customers;
        this.messages = messages;
        this.dispatches = dispatches;
        this.dispatchEvents = dispatchEvents;
        this.temperatures = temperatures;
        this.proofs = proofs;
        this.documents = documents;
        this.invoices = invoices;
        this.payments = payments;
        this.paymentMethods = paymentMethods;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public PagedResult<OrderSummaryReadModel> salesOrderSummaries(Long tenantId, Integer page, Integer pageSize) {
        List<SalesOrder> rows = orders.findByTenantIdOrderByIdAsc(tenantId).stream()
            .sorted(Comparator.comparing(SalesOrder::getCreatedAt).reversed())
            .toList();
        return page(rows.stream().map(order -> orderSummary(tenantId, order)).toList(), page, pageSize);
    }

    @Transactional(readOnly = true)
    public PagedResult<PurchaseRequestInboxReadModel> purchaseRequestInbox(Long tenantId, Integer page,
                                                                           Integer pageSize) {
        List<String> inboxStatuses = List.of("submitted", "buyer_adjustment_requested", "commercially_validated");
        List<PurchaseRequestInboxReadModel> rows = requests.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(request -> inboxStatuses.contains(request.getStatus()))
            .sorted(Comparator.comparing(PurchaseRequest::getUpdatedAt).reversed())
            .map(request -> requestInbox(tenantId, request))
            .toList();
        return page(rows, page, pageSize);
    }

    @Transactional(readOnly = true)
    public BuyerDashboardSummaryReadModel buyerDashboard(Long tenantId, Long clientAccountId) {
        Customer client = requireBuyerClient(tenantId, clientAccountId);
        List<PurchaseRequest> buyerRequests = requests.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(request -> client.getCode().equalsIgnoreCase(request.getClientId()))
            .toList();
        List<SalesOrder> buyerOrders = orders.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(order -> clientAccountId.equals(order.getCustomer().getId()))
            .toList();
        List<BusinessDocument> buyerDocuments = tenantDocuments(tenantId).stream()
            .filter(document -> clientAccountId.equals(document.getClientAccountId()))
            .toList();
        List<Invoice> buyerInvoices = invoices.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(invoice -> clientAccountId.equals(invoice.getOrder().getCustomer().getId()))
            .toList();
        List<NotificationRecord> buyerNotifications =
            notifications.findByTenantIdAndClientAccountIdOrderByReadAscIdDesc(tenantId, clientAccountId);

        return new BuyerDashboardSummaryReadModel(
            (int) buyerRequests.stream().filter(request -> !isClosedRequest(request.getStatus())).count(),
            (int) buyerOrders.stream().filter(order -> !isClosedOrder(order.getStatus())).count(),
            (int) buyerDocuments.stream().filter(document -> document.isVisibleToBuyer()
                && !"accepted".equalsIgnoreCase(document.getStatus())).count(),
            (int) buyerInvoices.stream().filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID
                && invoice.getStatus() != InvoiceStatus.VOIDED).count(),
            buyerRequests.stream()
                .sorted(Comparator.comparing(PurchaseRequest::getCreatedAt).reversed())
                .limit(5).map(request -> requestInbox(tenantId, request)).toList(),
            buyerOrders.stream()
                .sorted(Comparator.comparing(SalesOrder::getCreatedAt).reversed())
                .limit(5).map(order -> orderSummary(tenantId, order)).toList(),
            buyerNotifications.stream().limit(5).map(SalesBuyerReadModelService::notificationPreview).toList(),
            creditSummary(client));
    }

    @Transactional(readOnly = true)
    public OrderLifecycleReadModel buyerOrderLifecycle(Long tenantId, Long clientAccountId, Long orderId) {
        SalesOrder order = orders.findWithItemsByIdAndTenantId(orderId, tenantId)
            .filter(candidate -> clientAccountId.equals(candidate.getCustomer().getId()))
            .orElse(null);
        if (order == null) return null;

        List<DispatchOrder> orderDispatches = dispatches.findAllByOrderByIdAsc().stream()
            .filter(dispatch -> tenantId.equals(dispatch.getTenantId()) && orderId.equals(dispatch.getOrderId()))
            .toList();
        List<Long> dispatchIds = orderDispatches.stream().map(DispatchOrder::getId).toList();
        List<DispatchEvent> events = dispatchEvents.findAllByOrderByIdAsc().stream()
            .filter(event -> tenantId.equals(event.getTenantId()) && dispatchIds.contains(event.getDispatchOrderId())
                && event.isVisibleToBuyer())
            .sorted(Comparator.comparing(DispatchEvent::getCreatedAt))
            .toList();
        List<TemperatureLog> readings = temperatures.findAllByOrderByIdAsc().stream()
            .filter(log -> tenantId.equals(log.getTenantId())
                && (orderId.equals(log.getOrderId()) || dispatchIds.contains(log.getDispatchOrderId())))
            .sorted(Comparator.comparing(TemperatureLog::getRecordedAt).reversed())
            .toList();

        return new OrderLifecycleReadModel(
            orderSummary(tenantId, order),
            order.getItems().stream().map(SalesBuyerReadModelService::orderLine).toList(),
            orderDispatches.stream().map(dispatch -> dispatchSummary(tenantId, dispatch, order)).toList(),
            events.stream().map(SalesBuyerReadModelService::dispatchEvent).toList(),
            readings.stream().map(SalesBuyerReadModelService::temperatureReading).toList(),
            tenantDocuments(tenantId).stream()
                .filter(document -> orderId.equals(document.getOrderId()) && document.isVisibleToBuyer())
                .map(SalesBuyerReadModelService::documentPreview).toList(),
            invoices.findByTenantIdAndOrderIdOrderByIdAsc(tenantId, orderId).stream()
                .map(SalesBuyerReadModelService::invoicePreview).toList(),
            payments.findByTenantIdAndInvoiceOrderIdOrderByIdAsc(tenantId, orderId).stream()
                .map(SalesBuyerReadModelService::paymentPreview).toList());
    }

    @Transactional(readOnly = true)
    public ClientFinancialProfileReadModel buyerFinancialProfile(Long tenantId, Long clientAccountId) {
        Customer client = requireBuyerClient(tenantId, clientAccountId);
        List<InvoicePreviewReadModel> pendingInvoices = invoices.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(invoice -> clientAccountId.equals(invoice.getOrder().getCustomer().getId()))
            .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID && invoice.getStatus() != InvoiceStatus.VOIDED)
            .sorted(Comparator.comparing(Invoice::getCreatedAt).reversed())
            .limit(10).map(SalesBuyerReadModelService::invoicePreview).toList();
        List<PaymentPreviewReadModel> recentPayments = payments.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(payment -> clientAccountId.equals(payment.getClientAccountId()))
            .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
            .limit(10).map(SalesBuyerReadModelService::paymentPreview).toList();

        return new ClientFinancialProfileReadModel(
            clientSummary(client),
            creditSummary(client),
            (int) orders.findByTenantIdOrderByIdAsc(tenantId).stream()
                .filter(order -> clientAccountId.equals(order.getCustomer().getId()))
                .filter(order -> order.getStatus() == OrderStatus.PENDING).count(),
            pendingInvoices,
            recentPayments,
            (int) paymentMethods.findByTenantIdOrderByClientAccountIdAscIsDefaultDesc(tenantId).stream()
                .filter(method -> clientAccountId.equals(method.getClientAccountId())).count(),
            (int) tenantDocuments(tenantId).stream()
                .filter(document -> clientAccountId.equals(document.getClientAccountId())).count());
    }

    private OrderSummaryReadModel orderSummary(Long tenantId, SalesOrder order) {
        DispatchOrder dispatch = dispatches.findByTenantIdAndOrderId(tenantId, order.getId()).orElse(null);
        Payment payment = payments.findByTenantIdAndInvoiceOrderIdOrderByIdAsc(tenantId, order.getId()).stream()
            .max(Comparator.comparing(Payment::getCreatedAt)).orElse(null);
        LocalDate requestedDelivery = requests.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(request -> order.getId().equals(request.getAcceptedOrderId()))
            .map(PurchaseRequest::getRequestedDeliveryDate).findFirst().orElse(null);
        return new OrderSummaryReadModel(
            order.getId(),
            orderNumber(order.getId()),
            order.getStatus().name(),
            clientSummary(order.getCustomer()),
            order.total(),
            "PEN",
            order.getCreatedAt(),
            requestedDelivery,
            dispatch == null ? null : dispatch.getStatus(),
            payment == null ? null : payment.getStatus().name(),
            order.getItems().size());
    }

    private PurchaseRequestInboxReadModel requestInbox(Long tenantId, PurchaseRequest request) {
        Customer client = customers.findByTenantIdAndCode(tenantId, request.getClientId()).orElse(null);
        String lastMessage = messages.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(message -> request.getId().equals(message.getPurchaseRequestId()))
            .max(Comparator.comparing(ConversationMessage::getCreatedAt))
            .map(ConversationMessage::getBody).orElse(null);
        return new PurchaseRequestInboxReadModel(
            request.getId(), request.getCode(), client == null ? null : clientSummary(client),
            request.getStatus(), request.getPriority(), request.getCreatedAt(), request.getRequestedDeliveryDate(),
            request.getItems().size(), lastMessage, request.getCommercialOwner());
    }

    private DispatchOrderSummaryReadModel dispatchSummary(Long tenantId, DispatchOrder dispatch, SalesOrder order) {
        List<DispatchEventTimelineReadModel> events = dispatchEvents.findByDispatchOrderIdOrderByIdAsc(dispatch.getId())
            .stream().filter(event -> tenantId.equals(event.getTenantId()))
            .map(SalesBuyerReadModelService::dispatchEvent).toList();
        TemperatureReadingReadModel latestTemperature =
            temperatures.findByDispatchOrderIdOrderByIdAsc(dispatch.getId()).stream()
                .filter(log -> tenantId.equals(log.getTenantId()))
                .max(Comparator.comparing(TemperatureLog::getRecordedAt))
                .map(SalesBuyerReadModelService::temperatureReading).orElse(null);
        String podStatus = proofs.findByDispatchOrderIdOrderByIdAsc(dispatch.getId()).stream()
            .filter(proof -> tenantId.equals(proof.getTenantId()))
            .max(Comparator.comparing(ProofOfDeliveryRecord::getCreatedAt))
            .map(ProofOfDeliveryRecord::getStatus).orElse(null);
        return new DispatchOrderSummaryReadModel(
            dispatch.getId(), dispatch.getCode(), dispatch.getStatus(), dispatch.getRouteName(),
            dispatch.getResponsible(), dispatch.getEta(), dispatch.getDeliveryWindow(),
            orderSummary(tenantId, order), clientSummary(order.getCustomer()),
            events.isEmpty() ? null : events.get(events.size() - 1), events, podStatus, latestTemperature);
    }

    private Customer requireBuyerClient(Long tenantId, Long clientAccountId) {
        if (clientAccountId == null) throw new WorkspaceScopeException("Buyer client account is required.");
        return customers.findByIdAndTenantId(clientAccountId, tenantId)
            .orElseThrow(() -> new WorkspaceScopeException("Buyer client account is not available in this workspace."));
    }

    private List<BusinessDocument> tenantDocuments(Long tenantId) {
        return documents.findByTenantIdOrderByIdAsc(tenantId);
    }

    private static <T> PagedResult<T> page(List<T> rows, Integer requestedPage, Integer requestedPageSize) {
        int page = Math.max(requestedPage == null ? 1 : requestedPage, 1);
        int pageSize = Math.max(1, Math.min(requestedPageSize == null ? 25 : requestedPageSize, 100));
        int totalItems = rows.size();
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / pageSize);
        int from = Math.min((page - 1) * pageSize, totalItems);
        int to = Math.min(from + pageSize, totalItems);
        return new PagedResult<>(rows.subList(from, to), page, pageSize, totalItems, totalPages);
    }

    private static boolean isClosedRequest(String status) {
        return List.of("converted_to_order", "rejected", "cancelled").contains(status);
    }

    private static boolean isClosedOrder(OrderStatus status) {
        return status == OrderStatus.CANCELLED || status == OrderStatus.REJECTED;
    }

    private static String orderNumber(Long id) {
        return "ORD-2026-" + String.format("%04d", id);
    }

    private static ClientSummaryReadModel clientSummary(Customer client) {
        return new ClientSummaryReadModel(client.getId(), client.getCode(), client.getBusinessName(),
            client.getCommercialName());
    }

    private static CreditSummaryReadModel creditSummary(Customer client) {
        return new CreditSummaryReadModel(client.getMonthlyCreditLimit(), client.getMonthlyCreditUsed(),
            client.getMonthlyCreditAvailable(), client.getMonthlyCreditStatus(), false);
    }

    private static OrderLineReadModel orderLine(SalesOrderItem item) {
        return new OrderLineReadModel(item.getId(), item.getProduct().getSku(), item.getProduct().getSku(),
            item.getProduct().getName(), item.getQuantity(), item.getUnitPrice(), item.subtotal());
    }

    private static NotificationPreviewReadModel notificationPreview(NotificationRecord row) {
        return new NotificationPreviewReadModel(row.getId(), row.getTitle(), row.getBody(), row.isRead(),
            row.getCreatedAt());
    }

    private static BusinessDocumentPreviewReadModel documentPreview(BusinessDocument row) {
        return new BusinessDocumentPreviewReadModel(row.getId(), row.getType(), row.getLabel(), row.getStatus(),
            row.isVisibleToBuyer(), row.isRequired());
    }

    private static InvoicePreviewReadModel invoicePreview(Invoice row) {
        return new InvoicePreviewReadModel(row.getId(), row.getInvoiceNumber(), row.total(), row.getCurrency(),
            row.getStatus().name());
    }

    private static PaymentPreviewReadModel paymentPreview(Payment row) {
        return new PaymentPreviewReadModel(row.getId(), row.getReferenceCode(), row.getAmount(), row.getCurrency(),
            row.getStatus().name());
    }

    private static DispatchEventTimelineReadModel dispatchEvent(DispatchEvent row) {
        return new DispatchEventTimelineReadModel(row.getId(), row.getStatus(), row.getDescription(),
            row.isVisibleToBuyer(), row.getCreatedAt());
    }

    private static TemperatureReadingReadModel temperatureReading(TemperatureLog row) {
        return new TemperatureReadingReadModel(row.getId(), row.getCelsius(), row.getZone(), row.getStatus(),
            row.getRecordedAt());
    }

    public record ClientSummaryReadModel(Long id, String code, String businessName, String commercialName) { }
    public record OrderLineReadModel(Long id, String productId, String catalogItemId, String itemName, int quantity,
                                     BigDecimal unitPrice, BigDecimal subtotal) { }
    public record OrderSummaryReadModel(Long id, String orderNumber, String status, ClientSummaryReadModel client,
                                        BigDecimal total, String currency, OffsetDateTime createdAt,
                                        LocalDate requestedDeliveryDate, String dispatchStatus, String paymentStatus,
                                        int itemCount) { }
    public record PurchaseRequestInboxReadModel(Long id, String code, ClientSummaryReadModel client, String status,
                                                 String priority, OffsetDateTime createdAt,
                                                 LocalDate requestedDeliveryDate, int lineCount,
                                                 String lastMessagePreview, String commercialOwner) { }
    public record NotificationPreviewReadModel(Long id, String title, String body, boolean read,
                                                OffsetDateTime createdAt) { }
    public record BuyerDashboardSummaryReadModel(int activePurchaseRequestsCount, int activeOrdersCount,
                                                  int pendingDocumentsCount, int pendingInvoicesCount,
                                                  List<PurchaseRequestInboxReadModel> recentRequests,
                                                  List<OrderSummaryReadModel> recentOrders,
                                                  List<NotificationPreviewReadModel> notifications,
                                                  CreditSummaryReadModel creditSummary) { }
    public record CreditSummaryReadModel(BigDecimal creditLimit, BigDecimal usedCredit, BigDecimal availableCredit,
                                          String status, boolean estimated) { }
    public record BusinessDocumentPreviewReadModel(Long id, String type, String label, String status,
                                                    boolean visibleToBuyer, boolean required) { }
    public record InvoicePreviewReadModel(Long id, String invoiceNumber, BigDecimal amount, String currency,
                                          String paymentStatus) { }
    public record PaymentPreviewReadModel(Long id, String referenceCode, BigDecimal amount, String currency,
                                          String status) { }
    public record DispatchEventTimelineReadModel(Long id, String status, String description, boolean visibleToBuyer,
                                                  OffsetDateTime createdAt) { }
    public record TemperatureReadingReadModel(Long id, BigDecimal celsius, String zone, String status,
                                               OffsetDateTime recordedAt) { }
    public record DispatchOrderSummaryReadModel(Long id, String code, String status, String routeName,
                                                 String responsible, OffsetDateTime eta, String deliveryWindow,
                                                 OrderSummaryReadModel linkedOrder, ClientSummaryReadModel client,
                                                 DispatchEventTimelineReadModel lastEvent,
                                                 List<DispatchEventTimelineReadModel> events,
                                                 String proofOfDeliveryStatus,
                                                 TemperatureReadingReadModel latestTemperatureReading) { }
    public record OrderLifecycleReadModel(OrderSummaryReadModel order, List<OrderLineReadModel> items,
                                           List<DispatchOrderSummaryReadModel> dispatches,
                                           List<DispatchEventTimelineReadModel> dispatchEvents,
                                           List<TemperatureReadingReadModel> temperatureLogs,
                                           List<BusinessDocumentPreviewReadModel> businessDocuments,
                                           List<InvoicePreviewReadModel> invoices,
                                           List<PaymentPreviewReadModel> payments) { }
    public record ClientFinancialProfileReadModel(ClientSummaryReadModel client, CreditSummaryReadModel credit,
                                                   int openOrders, List<InvoicePreviewReadModel> pendingInvoices,
                                                   List<PaymentPreviewReadModel> recentPayments,
                                                   int paymentMethodsCount, int documentsCount) { }
}
