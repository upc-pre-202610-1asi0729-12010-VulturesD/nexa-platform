package com.nexa.platform.warehouse.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovementResource(Long id, @NotNull Long inventoryItemId, @NotBlank String type,
                               int quantityDelta, String note, int quantityAvailable) { }
