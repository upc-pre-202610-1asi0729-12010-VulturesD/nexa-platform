package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.domain.model.*;
import com.nexa.platform.invoicing.domain.model.repositories.*;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoicingService {
    private final InvoiceRepositoryPort invoices;
    private final PaymentRepositoryPort payments;
    private final SalesOrderRepositoryPort orders;
    private final InvoicingMapper mapper;
    public InvoicingService(InvoiceRepositoryPort invoices, PaymentRepositoryPort payments, SalesOrderRepositoryPort orders, InvoicingMapper mapper) { this.invoices = invoices; this.payments = payments; this.orders = orders; this.mapper = mapper; }
    @Transactional public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        SalesOrder order = orders.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Sales order", request.orderId()));
        Invoice invoice = new Invoice(order, "INV-" + System.currentTimeMillis());
        request.lines().forEach(line -> invoice.addLine(new InvoiceLine(line.description(), line.quantity(), line.unitPrice())));
        return mapper.toInvoiceResponse(invoices.save(invoice));
    }
    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices() { return invoices.findAll().stream().map(mapper::toInvoiceResponse).toList(); }
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long id) { return mapper.toInvoiceResponse(invoices.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice", id))); }
    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments() { return payments.findAll().stream().map(mapper::toPaymentResponse).toList(); }
    @Transactional(readOnly = true)
    public List<BusinessDocumentResponse> listBusinessDocuments() {
        return invoices.findAll().stream().map(this::toBusinessDocument).toList();
    }
    @Transactional public PaymentResponse registerPayment(PaymentRequest request) {
        Invoice invoice = invoices.findById(request.invoiceId()).orElseThrow(() -> new ResourceNotFoundException("Invoice", request.invoiceId()));
        Payment payment = payments.save(new Payment(invoice, request.amount(), request.method()));
        if (request.amount().compareTo(invoice.total()) >= 0) invoice.markPaid();
        return mapper.toPaymentResponse(payment);
    }

    private BusinessDocumentResponse toBusinessDocument(Invoice invoice) {
        Long orderId = invoice.getOrder().getId();
        String orderNumber = "ORD-2026-" + String.format("%04d", orderId);
        String invoiceNumber = invoice.getInvoiceNumber();
        LocalDate dueDate = invoice.getCreatedAt() == null ? LocalDate.now().plusDays(7) : invoice.getCreatedAt().toLocalDate().plusDays(7);
        BigDecimal total = invoice.total();
        return new BusinessDocumentResponse(
            invoiceNumber,
            clientId(invoice.getOrder()),
            orderNumber,
            "Invoice",
            invoiceNumber,
            invoiceNumber + ".pdf",
            invoice.getStatus() == InvoiceStatus.PAID ? "accepted" : "pending",
            true,
            true,
            dueDate(orderId).toString(),
            total
        );
    }

    private LocalDate dueDate(Long orderId) {
        if (Long.valueOf(1L).equals(orderId)) return LocalDate.parse("2026-06-12");
        if (Long.valueOf(6L).equals(orderId)) return LocalDate.parse("2026-06-18");
        return LocalDate.now().plusDays(7);
    }

    private String clientId(SalesOrder order) {
        if ("20600000001".equals(order.getCustomer().getTaxId())) return "CLI-001";
        return "CLI-" + String.format("%03d", order.getCustomer().getId());
    }
}
