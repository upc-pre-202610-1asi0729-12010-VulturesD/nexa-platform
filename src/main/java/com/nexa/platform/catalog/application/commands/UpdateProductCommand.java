package com.nexa.platform.catalog.application.commands;

import java.math.BigDecimal;

public record UpdateProductCommand(
    Long id,
    String name,
    String description,
    Long categoryId,
    String supplierName,
    BigDecimal unitPrice,
    String unit,
    String imageUrl,
    Integer minCelsius,
    Integer maxCelsius,
    String handlingNotes
) { }
