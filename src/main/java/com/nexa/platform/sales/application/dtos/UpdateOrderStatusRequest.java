package com.nexa.platform.sales.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequest(@NotBlank String status) { }
