package com.nexa.platform.shared.application.readmodels;

import java.math.BigDecimal;

public record PromotionalCatalogItemReadModel(
    Long id,
    String productId,
    String itemName,
    String brandName,
    String categoryName,
    BigDecimal unitPrice,
    String currency,
    String activePromotionCode,
    String activePromotionLabel,
    int availableStock
) { }
