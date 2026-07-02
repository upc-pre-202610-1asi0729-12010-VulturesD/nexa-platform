package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;

public record PurchaseRequestItemResponse(String productId, String name, int qty, String unit, BigDecimal price) { }
