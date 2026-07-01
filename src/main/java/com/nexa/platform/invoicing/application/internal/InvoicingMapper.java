package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class InvoicingMapper {
    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getTenantId(), invoice.getInvoiceNumber(),
            invoice.getOrder().getId(), invoice.getCurrency(), invoice.getStatus().name().toLowerCase(),
            invoice.total(), invoice.getPaidAt(), invoice.getLines().stream()
                .map(line -> new InvoiceLineResponse(line.getId(), line.getDescription(), line.getQuantity(),
                    line.getUnitPrice(), line.subtotal()))
                .toList());
    }
    public PaymentResponse toPaymentResponse(Payment payment) {
        Invoice invoice = payment.getInvoice();
        String orderNumber = "ORD-2026-" + String.format("%04d", invoice.getOrder().getId());
        return new PaymentResponse(payment.getReferenceCode(), payment.getId(), invoice.getId(), invoice.getInvoiceNumber(), orderNumber,
            payment.getReferenceCode(), payment.getAmount(), payment.getCurrency(), payment.getStatus().name().toLowerCase(),
            payment.getMethod(), payment.getTenantId(), payment.getClientAccountId(), payment.getPaymentMethodRecordId(),
            payment.getRejectionReason(), payment.getConfirmedAt(), payment.getRejectedAt(),
            payment.getCreatedAt(), payment.getUpdatedAt());
    }
    public PaymentProcessRecordDtos.PaymentProcessRecordResponse toPaymentProcessRecordResponse(PaymentProcessRecord record) {
        return new PaymentProcessRecordDtos.PaymentProcessRecordResponse(record.getId(), record.getTenantId(),
            record.getOrderId(), record.getClientAccountId(), record.getPaymentId(), record.getPaymentMethodRecordId(),
            record.getSubtotal(), record.getDiscount(), record.getShipping(), record.getIgv(), record.getTotal(),
            record.getStatus(), record.getCreatedAt(), record.getUpdatedAt());
    }
    public PaymentMethodRecordDtos.PaymentMethodRecordResponse toPaymentMethodRecordResponse(PaymentMethodRecord record) {
        return new PaymentMethodRecordDtos.PaymentMethodRecordResponse(record.getId(), record.getTenantId(),
            record.getClientAccountId(), record.getType(), record.getLabel(), record.getStatus(), record.isDefault(),
            record.getCreatedAt(), record.getUpdatedAt());
    }
    public BusinessDocumentDtos.BusinessDocumentA1Response toBusinessDocumentResponse(BusinessDocument document) {
        return new BusinessDocumentDtos.BusinessDocumentA1Response(document.getId(), document.getTenantId(),
            document.getOrderId(), document.getClientAccountId(), document.getDocumentTypeId(), document.getType(),
            document.getLabel(), document.getStatus(), document.getFileName(), document.isVisibleToBuyer(),
            document.isRequired(), document.getCreatedAt(), document.getUpdatedAt());
    }
}
