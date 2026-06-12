package com.nexa.platform.catalog.application.dtos;

import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String productCode, String name, String description, String category,
                              String supplierName, String brandName, BigDecimal unitPrice, String unit,
                              Integer minCelsius, Integer maxCelsius, String handlingNotes, String imageUrl,
                              String status, boolean active) { }
