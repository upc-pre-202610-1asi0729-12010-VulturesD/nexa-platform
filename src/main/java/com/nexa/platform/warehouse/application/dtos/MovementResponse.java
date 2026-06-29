package com.nexa.platform.warehouse.application.dtos;

public record MovementResponse(Long id, Long inventoryItemId, String type, int quantityDelta, String note, int quantityAvailable) { }
