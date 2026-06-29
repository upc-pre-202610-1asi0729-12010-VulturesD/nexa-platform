package com.nexa.platform.warehouse.interfaces.rest.resources;

public record InventoryResource(Long id, String warehouse, String productSku, String productName,
                                int quantityAvailable, int reorderPoint, boolean lowStock) { }
