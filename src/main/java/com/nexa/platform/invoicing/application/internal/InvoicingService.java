package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.application.outbound.BusinessDocumentContentGenerator;
import com.nexa.platform.invoicing.domain.model.*;
import com.nexa.platform.invoicing.domain.model.repositories.*;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import com.nexa.platform.shared.domain.model.DocumentType;
import com.nexa.platform.shared.domain.repositories.DocumentTypeRepositoryPort;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoicingService {
    private static final int MAX_DOCUMENT_BYTES = 20_000_000;
    private final InvoiceRepositoryPort invoices;
    private final PaymentRepositoryPort payments;
    private final PaymentProcessRecordRepositoryPort paymentProcesses;
    private final PaymentMethodRecordRepositoryPort paymentMethods;
    private final BusinessDocumentRepositoryPort businessDocuments;
    private final DocumentTypeRepositoryPort documentTypes;
    private final SalesOrderRepositoryPort orders;
    private final CustomerRepositoryPort customers;
    private final BusinessDocumentContentGenerator documentContentGenerator;
    private final InvoicingMapper mapper;
    public InvoicingService(InvoiceRepositoryPort invoices, PaymentRepositoryPort payments,
                            PaymentProcessRecordRepositoryPort paymentProcesses,
                            PaymentMethodRecordRepositoryPort paymentMethods,
                            BusinessDocumentRepositoryPort businessDocuments,
                            DocumentTypeRepositoryPort documentTypes, SalesOrderRepositoryPort orders,
                            CustomerRepositoryPort customers,
                            BusinessDocumentContentGenerator documentContentGenerator, InvoicingMapper mapper) {
        this.invoices = invoices;
        this.payments = payments;
        this.paymentProcesses = paymentProcesses;
        this.paymentMethods = paymentMethods;
        this.businessDocuments = businessDocuments;
        this.documentTypes = documentTypes;
        this.orders = orders;
        this.customers = customers;
        this.documentContentGenerator = documentContentGenerator;
        this.mapper = mapper;
    }
    @Transactional public InvoiceResponse createInvoice(Long tenantId, CreateInvoiceRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        SalesOrder order = findOrder(scopedTenantId, request.orderId());
        String invoiceNumber = normalizeInvoiceNumber(request.invoiceNumber());
        ensureInvoiceNumberAvailable(scopedTenantId, invoiceNumber, null);
        Invoice invoice = new Invoice(scopedTenantId, order, invoiceNumber, request.currency());
        request.lines().forEach(line -> invoice.addLine(new InvoiceLine(line.description(), line.quantity(), line.unitPrice())));
        return mapper.toInvoiceResponse(invoices.save(invoice));
    }
    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices(Long tenantId) {
        return invoices.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream().map(mapper::toInvoiceResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices(Long tenantId, Long clientAccountId) {
        Long scopedTenantId = requireTenant(tenantId);
        if (clientAccountId == null) return listInvoices(scopedTenantId);
        return invoices.findByTenantIdOrderByIdAsc(scopedTenantId).stream()
            .filter(invoice -> clientAccountId.equals(invoice.getOrder().getCustomer().getId()))
            .map(mapper::toInvoiceResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long tenantId, Long id) {
        return mapper.toInvoiceResponse(findInvoice(requireTenant(tenantId), id));
    }
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long tenantId, Long clientAccountId, Long id) {
        Invoice invoice = findInvoice(requireTenant(tenantId), id);
        if (clientAccountId != null && !clientAccountId.equals(invoice.getOrder().getCustomer().getId())) {
            throw new ResourceNotFoundException("Invoice", id);
        }
        return mapper.toInvoiceResponse(invoice);
    }
    @Transactional
    public InvoiceResponse updateInvoice(Long tenantId, Long id, UpdateInvoiceRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        Invoice invoice = findInvoice(scopedTenantId, id);
        SalesOrder order = findOrder(scopedTenantId, request.orderId());
        String invoiceNumber = normalizeInvoiceNumber(request.invoiceNumber());
        ensureInvoiceNumberAvailable(scopedTenantId, invoiceNumber, id);
        List<InvoiceLine> lines = request.lines().stream()
            .map(line -> new InvoiceLine(line.description(), line.quantity(), line.unitPrice()))
            .toList();
        invoice.update(order, invoiceNumber, request.currency(), lines);
        return mapper.toInvoiceResponse(invoices.save(invoice));
    }
    @Transactional
    public InvoiceResponse markInvoicePaid(Long tenantId, Long id) {
        Invoice invoice = findInvoice(requireTenant(tenantId), id);
        invoice.markPaid();
        return mapper.toInvoiceResponse(invoices.save(invoice));
    }
    @Transactional
    public void voidInvoice(Long tenantId, Long id) {
        Invoice invoice = findInvoice(requireTenant(tenantId), id);
        invoice.voidInvoice();
        invoices.save(invoice);
    }
    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(Long tenantId) {
        return payments.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream().map(mapper::toPaymentResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<PaymentResponse> listPaymentsByInvoice(Long tenantId, Long invoiceId) {
        requireTenant(tenantId);
        findInvoice(tenantId, invoiceId);
        return payments.findByTenantIdAndInvoiceIdOrderByIdAsc(tenantId, invoiceId).stream()
            .map(mapper::toPaymentResponse).toList();
    }
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long tenantId, Long id) {
        return mapper.toPaymentResponse(findPayment(requireTenant(tenantId), id));
    }
    @Transactional(readOnly = true)
    public List<BusinessDocumentDtos.BusinessDocumentA1Response> listBusinessDocuments(Long tenantId) {
        return businessDocuments.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream()
            .map(mapper::toBusinessDocumentResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<BusinessDocumentResponse> listBusinessDocumentCompatibility(Long tenantId) {
        return businessDocuments.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream()
            .map(this::toCompatibilityDocument).toList();
    }
    @Transactional(readOnly = true)
    public BusinessDocumentDtos.BusinessDocumentA1Response getBusinessDocument(Long tenantId, Long id) {
        return mapper.toBusinessDocumentResponse(findBusinessDocument(requireTenant(tenantId), id));
    }
    @Transactional(readOnly = true)
    public BusinessDocumentDtos.BusinessDocumentContentResponse getBusinessDocumentContent(Long tenantId, Long id) {
        BusinessDocument document = findBusinessDocument(requireTenant(tenantId), id);
        byte[] content = document.getContent();
        if (content == null || content.length == 0) throw new ResourceNotFoundException("Business document content", id);
        return new BusinessDocumentDtos.BusinessDocumentContentResponse(content, document.getFileName(),
            document.getContentType(), document.isVisibleToBuyer());
    }
    @Transactional
    public BusinessDocumentDtos.BusinessDocumentA1Response createBusinessDocument(Long tenantId, BusinessDocumentDtos.CreateBusinessDocumentRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        SalesOrder order = request.orderId() == null ? null : findOrder(scopedTenantId, request.orderId());
        validateClient(request.clientAccountId(), order);
        DocumentType documentType = resolveDocumentType(request.documentTypeId(), request.type());
        BusinessDocument document = new BusinessDocument(scopedTenantId, request.orderId(), request.clientAccountId(),
            documentType, request.label(), request.fileName(), request.visibleToBuyer(), request.required());
        return mapper.toBusinessDocumentResponse(businessDocuments.save(document));
    }
    @Transactional
    public BusinessDocumentDtos.BusinessDocumentA1Response generateBusinessDocument(Long tenantId, BusinessDocumentDtos.GenerateBusinessDocumentRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        SalesOrder order = findOrder(scopedTenantId, request.orderId());
        DocumentType documentType = resolveDocumentType(null, request.type());
        var generated = documentContentGenerator.generate(order, documentType.getKey());
        BusinessDocument document = businessDocuments.findByTenantIdAndOrderIdAndType(scopedTenantId, request.orderId(), documentType.getKey())
            .orElseGet(() -> new BusinessDocument(scopedTenantId, request.orderId(), generated.clientAccountId(),
                documentType, generated.label(), generated.fileName(), true, true));
        document.replaceGeneratedContent(documentType, generated.clientAccountId(), generated.label(),
            generated.fileName(), generated.contentType(), generated.content());
        return mapper.toBusinessDocumentResponse(businessDocuments.save(document));
    }
    @Transactional
    public BusinessDocumentDtos.BusinessDocumentA1Response uploadBusinessDocument(
        Long tenantId, BusinessDocumentDtos.UploadBusinessDocumentRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        if (request.content() == null || request.content().length == 0) {
            throw new IllegalArgumentException("Document file is required.");
        }
        if (request.content().length > MAX_DOCUMENT_BYTES) {
            throw new IllegalArgumentException("Document file cannot exceed 20 MB.");
        }
        SalesOrder order = request.orderId() == null ? null : findOrder(scopedTenantId, request.orderId());
        validateClient(request.clientAccountId(), order);
        String typeKey = request.type() == null || request.type().isBlank() ? "business_document" : request.type();
        DocumentType documentType = resolveDocumentType(null, typeKey);
        String label = request.label() == null || request.label().isBlank() ? documentType.getLabel() : request.label();
        BusinessDocument document = new BusinessDocument(scopedTenantId, request.orderId(), request.clientAccountId(),
            documentType, label, safeFileName(request.fileName()), request.visibleToBuyer(), request.required());
        String contentType = request.contentType() == null || request.contentType().isBlank()
            ? "application/octet-stream"
            : request.contentType().trim();
        document.attachContent(safeFileName(request.fileName()), contentType, request.content(), request.visibleToBuyer());
        return mapper.toBusinessDocumentResponse(businessDocuments.save(document));
    }
    @Transactional
    public BusinessDocumentDtos.BusinessDocumentA1Response changeBusinessDocumentStatus(
        Long tenantId, Long id, BusinessDocumentDtos.ChangeBusinessDocumentStatusRequest request) {
        BusinessDocument document = findBusinessDocument(requireTenant(tenantId), id);
        document.changeStatus(request.status(), request.visibleToBuyer());
        return mapper.toBusinessDocumentResponse(businessDocuments.save(document));
    }
    @Transactional
    public PaymentResponse registerPayment(Long tenantId, PaymentRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        Invoice invoice = findInvoice(scopedTenantId, request.invoiceId());
        Long clientAccountId = validatePaymentReferences(scopedTenantId, invoice, request.clientAccountId(),
            request.paymentMethodRecordId());
        String referenceCode = paymentReference(request.referenceCode());
        if (payments.findByReferenceCode(referenceCode).isPresent()) {
            throw new IllegalArgumentException("Payment reference code already exists.");
        }
        Payment payment = new Payment(scopedTenantId, invoice, clientAccountId, request.paymentMethodRecordId(),
            request.amount(), request.currency(), request.method(), referenceCode);
        return mapper.toPaymentResponse(payments.save(payment));
    }
    @Transactional
    public PaymentResponse updatePayment(Long tenantId, Long id, PaymentRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        Payment payment = findPayment(scopedTenantId, id);
        Invoice invoice = findInvoice(scopedTenantId, request.invoiceId());
        Long clientAccountId = validatePaymentReferences(scopedTenantId, invoice, request.clientAccountId(),
            request.paymentMethodRecordId());
        String referenceCode = paymentReference(request.referenceCode());
        payments.findByReferenceCode(referenceCode)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> { throw new IllegalArgumentException("Payment reference code already exists."); });
        payment.update(invoice, clientAccountId, request.paymentMethodRecordId(), request.amount(),
            request.currency(), request.method(), referenceCode);
        return mapper.toPaymentResponse(payments.save(payment));
    }
    @Transactional
    public PaymentResponse confirmPayment(Long tenantId, Long id) {
        Payment payment = findPayment(requireTenant(tenantId), id);
        payment.confirm();
        if (payment.getAmount().compareTo(payment.getInvoice().total()) >= 0) {
            payment.getInvoice().markPaid();
        }
        return mapper.toPaymentResponse(payments.save(payment));
    }
    @Transactional
    public PaymentResponse rejectPayment(Long tenantId, Long id, String reason) {
        Payment payment = findPayment(requireTenant(tenantId), id);
        payment.reject(reason);
        return mapper.toPaymentResponse(payments.save(payment));
    }
    @Transactional
    public PaymentResponse cancelPayment(Long tenantId, Long id) {
        Payment payment = findPayment(requireTenant(tenantId), id);
        payment.cancel();
        return mapper.toPaymentResponse(payments.save(payment));
    }
    @Transactional(readOnly = true)
    public List<PaymentProcessRecordDtos.PaymentProcessRecordResponse> listPaymentProcesses(Long tenantId) {
        return paymentProcesses.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream()
            .map(mapper::toPaymentProcessRecordResponse).toList();
    }
    @Transactional(readOnly = true)
    public PaymentProcessRecordDtos.PaymentProcessRecordResponse getPaymentProcess(Long tenantId, Long id) {
        return mapper.toPaymentProcessRecordResponse(findPaymentProcess(requireTenant(tenantId), id));
    }
    @Transactional
    public PaymentProcessRecordDtos.PaymentProcessRecordResponse createPaymentProcess(Long tenantId, PaymentProcessRecordDtos.CreatePaymentProcessRecordRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        if (request.orderId() != null) findOrder(scopedTenantId, request.orderId());
        PaymentProcessRecord record = new PaymentProcessRecord(scopedTenantId, request.orderId(), request.clientAccountId(),
            request.paymentId(), request.paymentMethodRecordId(), request.subtotal(), request.discount(), request.shipping(),
            request.igv(), request.total(), request.status());
        return mapper.toPaymentProcessRecordResponse(paymentProcesses.save(record));
    }
    @Transactional
    public PaymentProcessRecordDtos.PaymentProcessRecordResponse changePaymentProcessStatus(Long tenantId, Long id, String status) {
        PaymentProcessRecord record = findPaymentProcess(requireTenant(tenantId), id);
        record.changeStatus(status);
        return mapper.toPaymentProcessRecordResponse(paymentProcesses.save(record));
    }
    @Transactional(readOnly = true)
    public List<PaymentMethodRecordDtos.PaymentMethodRecordResponse> listPaymentMethods(Long tenantId) {
        requireTenant(tenantId);
        return paymentMethods.findByTenantIdOrderByClientAccountIdAscIsDefaultDesc(tenantId).stream()
            .map(mapper::toPaymentMethodRecordResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public PaymentMethodRecordDtos.PaymentMethodRecordResponse getPaymentMethod(Long tenantId, Long id) {
        return mapper.toPaymentMethodRecordResponse(findPaymentMethod(requireTenant(tenantId), id));
    }
    @Transactional
    public PaymentMethodRecordDtos.PaymentMethodRecordResponse createPaymentMethod(
        Long tenantId, PaymentMethodRecordDtos.CreatePaymentMethodRecordRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        customers.findByIdAndTenantId(request.clientAccountId(), scopedTenantId)
            .orElseThrow(() -> new IllegalArgumentException("Client account does not belong to the current tenant."));
        if (request.isDefault()) clearPaymentMethodDefaults(scopedTenantId, request.clientAccountId(), null);
        PaymentMethodRecord record = new PaymentMethodRecord(scopedTenantId, request.clientAccountId(),
            request.type(), request.label(), request.isDefault());
        return mapper.toPaymentMethodRecordResponse(paymentMethods.save(record));
    }
    @Transactional
    public PaymentMethodRecordDtos.PaymentMethodRecordResponse changePaymentMethodStatus(
        Long tenantId, Long id, PaymentMethodRecordDtos.ChangePaymentMethodRecordStatusRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        PaymentMethodRecord record = findPaymentMethod(scopedTenantId, id);
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearPaymentMethodDefaults(scopedTenantId, record.getClientAccountId(), record.getId());
        }
        record.changeStatus(request.status(), request.isDefault());
        return mapper.toPaymentMethodRecordResponse(paymentMethods.save(record));
    }

    private BusinessDocumentResponse toCompatibilityDocument(BusinessDocument document) {
        SalesOrder order = document.getOrderId() == null ? null : findOrder(document.getTenantId(), document.getOrderId());
        String orderNumber = document.getOrderId() == null ? "" : "ORD-2026-" + String.format("%04d", document.getOrderId());
        BigDecimal total = order == null ? BigDecimal.ZERO : order.total();
        LocalDate dueDate = document.getCreatedAt() == null
            ? LocalDate.now().plusDays(7)
            : document.getCreatedAt().toLocalDate().plusDays(7);
        return new BusinessDocumentResponse(
            String.valueOf(document.getId()),
            order == null ? compatibilityClientId(document.getClientAccountId()) : clientId(order),
            orderNumber,
            document.getType(),
            document.getLabel(),
            document.getFileName(),
            document.getStatus(),
            document.isVisibleToBuyer(),
            document.isRequired(),
            dueDate.toString(),
            total
        );
    }

    private String clientId(SalesOrder order) {
        if ("20600000001".equals(order.getCustomer().getTaxId())) return "CLI-001";
        return "CLI-" + String.format("%03d", order.getCustomer().getId());
    }

    private String compatibilityClientId(Long clientAccountId) {
        return clientAccountId == null ? "" : "CLI-" + String.format("%03d", clientAccountId);
    }

    private SalesOrder findOrder(Long tenantId, Long id) {
        SalesOrder order = orders.findWithItemsById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sales order", id));
        if (!tenantId.equals(order.getTenantId())) throw new ResourceNotFoundException("Sales order", id);
        return order;
    }

    private void validateClient(Long clientAccountId, SalesOrder order) {
        if (clientAccountId != null && order != null && !clientAccountId.equals(order.getCustomer().getId())) {
            throw new IllegalArgumentException("Client account does not belong to the selected order.");
        }
    }

    private DocumentType resolveDocumentType(Long documentTypeId, String type) {
        if (documentTypeId != null) {
            DocumentType documentType = documentTypes.findById(documentTypeId)
                .filter(DocumentType::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Document type is not active."));
            if (type != null && !type.isBlank() && !documentType.getKey().equals(type.trim().toLowerCase())) {
                throw new IllegalArgumentException("Document type id does not match document type key.");
            }
            return documentType;
        }
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Document type is required.");
        return documentTypes.findByKeyAndActiveTrue(type.trim().toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("Document type is not active."));
    }

    private static String safeFileName(String fileName) {
        String normalized = fileName == null ? "" : fileName.replace('\\', '/').trim();
        int separator = normalized.lastIndexOf('/');
        String safe = separator >= 0 ? normalized.substring(separator + 1) : normalized;
        if (safe.isBlank()) safe = "document.bin";
        return safe.length() <= 240 ? safe : safe.substring(safe.length() - 240);
    }

    private BusinessDocument findBusinessDocument(Long tenantId, Long id) {
        return businessDocuments.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Business document", id));
    }

    private Invoice findInvoice(Long tenantId, Long id) {
        return invoices.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", id));
    }

    private String normalizeInvoiceNumber(String value) {
        return value == null || value.isBlank()
            ? "INV-" + System.currentTimeMillis()
            : value.trim().toUpperCase();
    }

    private void ensureInvoiceNumberAvailable(Long tenantId, String invoiceNumber, Long excludedId) {
        invoices.findByTenantIdAndInvoiceNumber(tenantId, invoiceNumber)
            .filter(existing -> excludedId == null || !existing.getId().equals(excludedId))
            .ifPresent(existing -> { throw new IllegalArgumentException("Invoice number already exists."); });
    }

    private Payment findPayment(Long tenantId, Long id) {
        return payments.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    private Long validatePaymentReferences(Long tenantId, Invoice invoice, Long requestedClientAccountId,
                                           Long paymentMethodRecordId) {
        Long invoiceClientId = invoice.getOrder().getCustomer().getId();
        Long clientAccountId = requestedClientAccountId == null ? invoiceClientId : requestedClientAccountId;
        if (!invoiceClientId.equals(clientAccountId)) {
            throw new IllegalArgumentException("Client account does not belong to the selected invoice.");
        }
        if (paymentMethodRecordId != null) {
            findPaymentMethod(tenantId, paymentMethodRecordId);
        }
        return clientAccountId;
    }

    private static String paymentReference(String value) {
        return value == null || value.isBlank()
            ? "PAY-" + System.currentTimeMillis()
            : value.trim().toUpperCase();
    }

    private PaymentMethodRecord findPaymentMethod(Long tenantId, Long id) {
        return paymentMethods.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment method record", id));
    }

    private void clearPaymentMethodDefaults(Long tenantId, Long clientAccountId, Long excludedId) {
        paymentMethods.findByTenantIdAndClientAccountIdAndIsDefaultTrue(tenantId, clientAccountId).stream()
            .filter(record -> excludedId == null || !excludedId.equals(record.getId()))
            .forEach(record -> {
                record.clearDefault();
                paymentMethods.save(record);
            });
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        return tenantId;
    }

    private PaymentProcessRecord findPaymentProcess(Long tenantId, Long id) {
        return paymentProcesses.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment process record", id));
    }
}
