package com.nexa.platform.logistics.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateShipmentRequest(@NotNull Long orderId, @NotNull Long routeId, @NotBlank String carrier, String trackingNote) { }
