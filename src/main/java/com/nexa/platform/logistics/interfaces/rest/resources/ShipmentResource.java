package com.nexa.platform.logistics.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShipmentResource(Long id, @NotNull Long orderId, @NotNull Long routeId, String route,
                               @NotBlank String carrier, String status, String trackingNote) { }
