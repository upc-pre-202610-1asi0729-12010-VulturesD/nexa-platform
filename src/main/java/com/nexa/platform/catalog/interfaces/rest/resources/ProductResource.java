package com.nexa.platform.catalog.interfaces.rest.resources;

import java.math.BigDecimal;

public record ProductResource(
    Long id,
    String sku,
    String productCode,
    String name,
    String description,
    String category,
    String supplierName,
    String brandName,
    BigDecimal unitPrice,
    String unit,
    Integer minCelsius,
    Integer maxCelsius,
    String handlingNotes,
    String imageUrl,
    String status,
    boolean active,
    String brand,
    BigDecimal price,
    String availability,
    Integer stock,
    String temperatureRange,
    String catalogItemId,
    String productId,
    String itemName,
    String categoryName,
    BigDecimal unitPriceAmount,
    String unitPriceCurrency,
    Integer availableStock,
    Integer reservedStock,
    Integer minStock,
    String coldChainRequirement,
    Boolean isActive
) { }
