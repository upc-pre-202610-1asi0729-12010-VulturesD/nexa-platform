package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpsertPurchaseRequestLineResource(
    Long tenantId,
    @NotNull @Positive Long purchaseRequestId,
    @NotNull @Positive Long catalogItemId,
    @NotNull @Positive BigDecimal quantity,
    @Size(max = 32) String unit,
    BigDecimal estimatedWeightKg,
    @Size(max = 240) String notes
) { }
