package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderItemResource(@NotNull Long productId, String sku, String name, @Min(1) int quantity,
                                BigDecimal unitPrice, BigDecimal subtotal) { }
