package com.nexa.platform.warehouse.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class InventoryOperationDtos {
    private InventoryOperationDtos() { }

    public record InventoryLotResponse(Long backendId, Long tenantId, Long inventoryItemId, String productId,
                                       String catalogItemId, Long warehouseId, String warehouse, String id,
                                       String lotCode, int qty, int reserved, LocalDate entryDate, LocalDate expiry,
                                       String zone, String status, BigDecimal minimumTemperature,
                                       BigDecimal maximumTemperature, OffsetDateTime createdAt,
                                       OffsetDateTime updatedAt) { }
    public record UpsertInventoryLotRequest(Long inventoryItemId, String productId, Long warehouseId,
                                            String warehouse, String lotCode, int quantity, int reservedQuantity,
                                            LocalDate entryDate, LocalDate expirationDate, String zone, String status,
                                            BigDecimal minimumTemperature, BigDecimal maximumTemperature) { }
    public record InventoryReservationResponse(Long id, Long tenantId, String code, Long inventoryItemId,
                                               String productId, String lotCode, String orderId,
                                               Long purchaseRequestId, int units, String status,
                                               OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record CreateInventoryReservationRequest(String code, Long inventoryItemId, String productId,
                                                    String lotCode, String orderId, Long purchaseRequestId,
                                                    int units) { }
    public record InventoryMovementResponse(Long backendId, Long tenantId, String id, String code,
                                            Long inventoryItemId, String productId, String lotId,
                                            String warehouse, String type, int qty, String orderId,
                                            String reason, String note, BigDecimal temperatureReading,
                                            String user, OffsetDateTime date, OffsetDateTime createdAt) { }
    public record CreateInventoryMovementRequest(String code, Long inventoryItemId, String productId,
                                                 String warehouse, String lotCode, String type, int quantity,
                                                 String orderId, String reason, String note,
                                                 BigDecimal temperatureReading, String user,
                                                 OffsetDateTime occurredAt, LocalDate expirationDate) { }
}
