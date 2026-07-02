package com.nexa.platform.sales.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderRequest(@NotNull Long customerId, @NotEmpty List<@Valid OrderItemRequest> items) { }
