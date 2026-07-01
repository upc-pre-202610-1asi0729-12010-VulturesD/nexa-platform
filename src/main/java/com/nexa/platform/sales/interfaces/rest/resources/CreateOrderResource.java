package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateOrderResource(@NotNull Long customerId, @NotEmpty List<@Valid OrderItemResource> items) { }
