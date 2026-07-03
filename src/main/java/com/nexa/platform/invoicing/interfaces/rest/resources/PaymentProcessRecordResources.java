package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class PaymentProcessRecordResources {
    private PaymentProcessRecordResources() { }

    public record PaymentProcessRecordResource(Long id, Long tenantId, Long orderId, Long clientAccountId, Long paymentId,
                                               Long paymentMethodRecordId, BigDecimal subtotal, BigDecimal discount,
                                               BigDecimal shipping, BigDecimal igv, BigDecimal total, String status,
                                               OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreatePaymentProcessRecordResource(Long tenantId, Long orderId, Long clientAccountId, Long paymentId,
                                                     Long paymentMethodRecordId,
                                                     @NotNull @DecimalMin("0.00") BigDecimal subtotal,
                                                     @NotNull @DecimalMin("0.00") BigDecimal discount,
                                                     @NotNull @DecimalMin("0.00") BigDecimal shipping,
                                                     @NotNull @DecimalMin("0.00") BigDecimal igv,
                                                     @NotNull @DecimalMin("0.00") BigDecimal total,
                                                     String status) { }
    public record ChangePaymentProcessStatusResource(@NotBlank String status) { }
}
