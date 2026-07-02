package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class CreditRequestDtos {
    private CreditRequestDtos() { }

    public record CreateCreditRequestRequest(Long clientAccountId, String code, BigDecimal requestedAmount,
                                             String reason) { }

    public record ResolveCreditRequestRequest(String status, String reviewedBy, String note) { }

    public record CreditRequestResponse(Long id, Long tenantId, Long clientAccountId, String code,
                                        BigDecimal requestedAmount, String reason, String status,
                                        Long createdByUserId, String reviewedBy, String resolutionNote,
                                        OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
