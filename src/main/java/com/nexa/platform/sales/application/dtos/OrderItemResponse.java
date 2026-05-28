package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String sku, String name, int quantity, BigDecimal unitPrice, BigDecimal subtotal) { }
