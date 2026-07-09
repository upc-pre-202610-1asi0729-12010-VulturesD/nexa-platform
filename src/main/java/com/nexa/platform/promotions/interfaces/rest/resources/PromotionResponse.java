package com.nexa.platform.promotions.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record PromotionResponse(
    Long id,
    Long tenantId,
    String code,
    String name,
    String campaign,
    String description,
    String discountLabel,
    String visibility,
    String commercialRule,
    String adjustmentType,
    String targetSegment,
    String notes,
    String catalogScope,
    LocalDate startsOn,
    LocalDate endsOn,
    String status,
    List<String> productIds,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
