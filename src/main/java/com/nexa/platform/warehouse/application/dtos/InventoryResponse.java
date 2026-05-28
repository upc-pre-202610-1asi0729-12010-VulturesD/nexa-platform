package com.nexa.platform.warehouse.application.dtos;

public record InventoryResponse(Long id, String warehouse, String productSku, String productName, int quantityAvailable, int reorderPoint, boolean lowStock) { }
