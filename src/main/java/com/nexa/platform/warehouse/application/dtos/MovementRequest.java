package com.nexa.platform.warehouse.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovementRequest(@NotNull Long inventoryItemId, @NotBlank String type, int quantityDelta, String note) { }
