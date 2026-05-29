package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class InvoicingMapper {
    public InvoiceResponse toInvoiceResponse(Invoice invoice) { return new InvoiceResponse(invoice.getId(), invoice.getInvoiceNumber(), invoice.getOrder().getId(), invoice.getStatus().name(), invoice.total()); }
    public PaymentResponse toPaymentResponse(Payment payment) { return new PaymentResponse(payment.getId(), payment.getInvoice().getId(), payment.getAmount(), payment.getMethod()); }
}
