package com.nexa.platform.catalog.application.dtos;

import java.math.BigDecimal;

public record ProductResponse(Long id, String sku, String name, String description, String category, String supplierName,
                              BigDecimal unitPrice, Integer minCelsius, Integer maxCelsius, String handlingNotes, boolean active) { }
