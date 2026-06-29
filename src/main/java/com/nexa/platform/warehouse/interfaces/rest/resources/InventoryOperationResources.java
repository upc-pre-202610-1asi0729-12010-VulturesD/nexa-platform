package com.nexa.platform.warehouse.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class InventoryOperationResources {
    private InventoryOperationResources() { }

    public record InventoryLotResource(Long backendId, Long tenantId, Long inventoryItemId, String productId,
                                       String catalogItemId, Long warehouseId, String warehouse, String id,
                                       String lotCode, int qty, int reserved, LocalDate entryDate, LocalDate expiry,
                                       String zone, String status, BigDecimal minimumTemperature,
                                       BigDecimal maximumTemperature, OffsetDateTime createdAt,
                                       OffsetDateTime updatedAt) { }
    public record UpsertInventoryLotResource(Long inventoryItemId, String productId, Long warehouseId,
                                             String warehouse, String id, String lotCode,
                                             @Min(0) Integer quantity, @Min(0) Integer qty,
                                             @Min(0) Integer reservedQuantity, @Min(0) Integer reserved,
                                             LocalDate entryDate, LocalDate expirationDate, LocalDate expiry,
                                             String zone, String status, BigDecimal minimumTemperature,
                                             BigDecimal maximumTemperature) { }
    public record InventoryReservationResource(Long id, Long tenantId, String code, Long inventoryItemId,
                                               String productId, String lotCode, String orderId,
                                               Long purchaseRequestId, int units, String status,
                                               OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreateInventoryReservationResource(String id, String code, Long inventoryItemId,
                                                     String productId, String lotId, String lotCode,
                                                     String orderId, Long purchaseRequestId,
                                                     @Min(0) Integer units, @Min(0) Integer quantity) { }
    public record InventoryMovementResource(Long backendId, Long tenantId, String id, String code,
                                            Long inventoryItemId, String productId, String lotId,
                                            String warehouse, String type, int qty, String orderId,
                                            String reason, String note, BigDecimal temperatureReading,
                                            String user, OffsetDateTime date, OffsetDateTime createdAt) { }
    public record CreateInventoryMovementResource(String id, String code, Long inventoryItemId,
                                                  String productId, Long warehouseId, String warehouse,
                                                  String lotId, String lotNumber, String type,
                                                  String movementType, int quantity, Integer qty,
                                                  String orderId, String reference, String reason,
                                                  String note, String notes, BigDecimal temperatureReading,
                                                  String user, OffsetDateTime occurredAt,
                                                  LocalDate expirationDate) { }
}
