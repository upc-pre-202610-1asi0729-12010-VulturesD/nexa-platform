package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PurchaseRequestLineResource(
    Long id,
    Long tenantId,
    Long purchaseRequestId,
    Long catalogItemId,
    BigDecimal quantity,
    String unit,
    BigDecimal estimatedWeightKg,
    String notes,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) { }
