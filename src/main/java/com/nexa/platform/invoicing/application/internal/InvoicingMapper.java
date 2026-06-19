package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class InvoicingMapper {
    public InvoiceResponse toInvoiceResponse(Invoice invoice) { return new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(), invoice.getOrder().getId(), invoice.getStatus().name(), invoice.total()); }
    public PaymentResponse toPaymentResponse(Payment payment) {
        Invoice invoice = payment.getInvoice();
        String orderNumber = "ORD-2026-" + String.format("%04d", invoice.getOrder().getId());
        String referenceCode = "PAY-2026-" + String.format("%04d", invoice.getOrder().getId());
        String status = invoice.getStatus() == InvoiceStatus.PAID ? "paid" : "pending";
        return new PaymentResponse(referenceCode, payment.getId(), invoice.getId(), invoice.getInvoiceNumber(), orderNumber,
            referenceCode, payment.getAmount(), "PEN", status, payment.getMethod());
    }
}
