package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;

public record PurchaseRequestItemResource(String productId, String name, int qty, String unit, BigDecimal price) { }
