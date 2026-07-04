package com.nexa.platform.invoicing.application.dtos;

import java.time.OffsetDateTime;

public final class PaymentMethodRecordDtos {
    private PaymentMethodRecordDtos() { }

    public record PaymentMethodRecordResponse(Long id, Long tenantId, Long clientAccountId, String type,
                                              String label, String status, boolean isDefault,
                                              OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreatePaymentMethodRecordRequest(Long clientAccountId, String type, String label,
                                                   boolean isDefault) { }
    public record ChangePaymentMethodRecordStatusRequest(String status, Boolean isDefault) { }
}
