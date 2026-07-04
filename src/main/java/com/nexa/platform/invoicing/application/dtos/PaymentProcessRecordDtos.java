package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class PaymentProcessRecordDtos {
    private PaymentProcessRecordDtos() { }

    public record PaymentProcessRecordResponse(Long id, Long tenantId, Long orderId, Long clientAccountId, Long paymentId,
                                               Long paymentMethodRecordId, BigDecimal subtotal, BigDecimal discount,
                                               BigDecimal shipping, BigDecimal igv, BigDecimal total, String status,
                                               OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreatePaymentProcessRecordRequest(Long tenantId, Long orderId, Long clientAccountId, Long paymentId,
                                                    Long paymentMethodRecordId, BigDecimal subtotal, BigDecimal discount,
                                                    BigDecimal shipping, BigDecimal igv, BigDecimal total,
                                                    String status) { }
    public record ChangePaymentProcessStatusRequest(String status) { }
}
