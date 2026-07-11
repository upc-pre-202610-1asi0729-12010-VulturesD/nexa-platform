package com.nexa.platform.promotions.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record UpsertPromotionResource(
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
    String startDate,
    String endDate,
    String status,
    List<String> productIds
) { }
