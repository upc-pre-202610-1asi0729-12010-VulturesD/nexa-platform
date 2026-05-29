package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.domain.model.*;
import com.nexa.platform.invoicing.infrastructure.persistence.jpa.*;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.infrastructure.persistence.jpa.SalesOrderRepository;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoicingService {
    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final SalesOrderRepository orders;
    private final InvoicingMapper mapper;
    public InvoicingService(InvoiceRepository invoices, PaymentRepository payments, SalesOrderRepository orders, InvoicingMapper mapper) { this.invoices = invoices; this.payments = payments; this.orders = orders; this.mapper = mapper; }
    @Transactional public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        SalesOrder order = orders.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Sales order", request.orderId()));
        Invoice invoice = new Invoice(order, "INV-" + System.currentTimeMillis());
        request.lines().forEach(line -> invoice.addLine(new InvoiceLine(line.description(), line.quantity(), line.unitPrice())));
        return mapper.toInvoiceResponse(invoices.save(invoice));
    }
    public List<InvoiceResponse> listInvoices() { return invoices.findAll().stream().map(mapper::toInvoiceResponse).toList(); }
    public InvoiceResponse getInvoice(Long id) { return mapper.toInvoiceResponse(invoices.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice", id))); }
    @Transactional public PaymentResponse registerPayment(PaymentRequest request) {
        Invoice invoice = invoices.findById(request.invoiceId()).orElseThrow(() -> new ResourceNotFoundException("Invoice", request.invoiceId()));
        Payment payment = payments.save(new Payment(invoice, request.amount(), request.method()));
        if (request.amount().compareTo(invoice.total()) >= 0) invoice.markPaid();
        return mapper.toPaymentResponse(payment);
    }
}
