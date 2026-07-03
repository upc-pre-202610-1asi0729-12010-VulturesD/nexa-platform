package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;

public final class PaymentMethodRecordResources {
    private PaymentMethodRecordResources() { }

    public record PaymentMethodRecordResource(Long id, Long tenantId, Long clientAccountId, String type,
                                              String label, String status, boolean isDefault,
                                              OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreatePaymentMethodRecordResource(@NotNull @Positive Long clientAccountId,
                                                    @NotBlank String type, @NotBlank String label,
                                                    boolean isDefault) { }
    public record ChangePaymentMethodRecordStatusResource(@NotBlank String status, Boolean isDefault) { }
}
