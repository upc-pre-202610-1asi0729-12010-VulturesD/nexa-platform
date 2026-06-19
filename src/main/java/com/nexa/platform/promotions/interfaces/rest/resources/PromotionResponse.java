package com.nexa.platform.promotions.interfaces.rest.resources;

import java.util.List;

public record PromotionResponse(
    String id,
    String name,
    String description,
    String discountLabel,
    String visibility,
    String status,
    List<String> productIds,
    String notes
) {}
