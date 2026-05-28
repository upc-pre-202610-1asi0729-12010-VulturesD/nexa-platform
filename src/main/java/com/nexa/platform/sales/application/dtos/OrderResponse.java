package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(Long id, String customer, String status, List<OrderItemResponse> items, BigDecimal total) { }
