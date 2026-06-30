package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class CreditRequestResources {
    private CreditRequestResources() { }

    public record CreateCreditRequestResource(Long clientAccountId, String code,
                                              @NotNull @DecimalMin(value = "0.01") BigDecimal requestedAmount,
                                              @NotBlank String reason) { }

    public record ResolveCreditRequestResource(@NotBlank String status, String reviewedBy, String note) { }

    public record CreditRequestResource(Long id, Long tenantId, Long clientAccountId, String code,
                                        BigDecimal requestedAmount, String reason, String status,
                                        Long createdByUserId, String reviewedBy, String resolutionNote,
                                        OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
