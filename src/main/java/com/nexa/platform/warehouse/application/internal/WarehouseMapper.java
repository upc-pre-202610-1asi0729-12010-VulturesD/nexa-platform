package com.nexa.platform.warehouse.application.internal;

import com.nexa.platform.warehouse.application.dtos.*;
import com.nexa.platform.warehouse.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {
    public WarehouseResponse toWarehouseResponse(Warehouse warehouse) { return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getAddress(), warehouse.getTemperatureBand().name()); }
    public InventoryResponse toInventoryResponse(InventoryItem item) { return new InventoryResponse(item.getId(), item.getWarehouse().getName(), item.getProduct().getSku(), item.getProduct().getName(), item.getQuantityAvailable(), item.getReorderPoint(), item.isLowStock()); }
    public MovementResponse toMovementResponse(InventoryMovement movement) { return new MovementResponse(movement.getId(), movement.getItem().getId(), movement.getType().name(), movement.getQuantityDelta(), movement.getNote(), movement.getItem().getQuantityAvailable()); }
    public InventoryOperationDtos.InventoryLotResponse toInventoryLotResponse(StockBatch lot) {
        InventoryItem item = lot.getItem();
        return new InventoryOperationDtos.InventoryLotResponse(lot.getId(), lot.getTenantId(), item.getId(),
            item.getProduct().getSku(), item.getProduct().getSku(), item.getWarehouse().getId(),
            item.getWarehouse().getName(), lot.getLotCode(), lot.getLotCode(), lot.getQuantity(),
            lot.getReservedQuantity(), lot.getEntryDate(), lot.getExpirationDate(), lot.getZone(), lot.getStatus(),
            lot.getMinimumTemperature(), lot.getMaximumTemperature(), null, null);
    }
    public InventoryOperationDtos.InventoryReservationResponse toInventoryReservationResponse(
        InventoryReservationRecord reservation) {
        return new InventoryOperationDtos.InventoryReservationResponse(reservation.getId(), reservation.getTenantId(),
            reservation.getCode(), reservation.getInventoryItem().getId(),
            reservation.getInventoryItem().getProduct().getSku(),
            reservation.getInventoryLot() == null ? null : reservation.getInventoryLot().getLotCode(),
            reservation.getOrderId() == null ? null : "ORD-2026-" + String.format("%04d", reservation.getOrderId()),
            reservation.getPurchaseRequestId(), reservation.getUnits(), reservation.getStatus(),
            reservation.getCreatedAt(), reservation.getUpdatedAt());
    }
}
