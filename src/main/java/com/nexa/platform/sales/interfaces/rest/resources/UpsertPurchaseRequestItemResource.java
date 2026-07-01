package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpsertPurchaseRequestItemResource(
    Long catalogItemId,
    @Size(max = 40) String productId,
    @NotNull @Positive BigDecimal quantity,
    @Size(max = 32) String unit,
    BigDecimal estimatedWeightKg,
    @Size(max = 240) String notes
) { }
