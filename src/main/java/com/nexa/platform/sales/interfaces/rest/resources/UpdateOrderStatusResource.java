package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusResource(@NotBlank String status) { }
