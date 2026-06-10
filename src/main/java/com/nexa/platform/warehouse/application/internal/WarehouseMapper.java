package com.nexa.platform.warehouse.application.internal;

import com.nexa.platform.warehouse.application.dtos.*;
import com.nexa.platform.warehouse.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {
    public WarehouseResponse toWarehouseResponse(Warehouse warehouse) { return new WarehouseResponse(warehouse.getId(), warehouse.getName(), warehouse.getAddress(), warehouse.getTemperatureBand().name()); }
    public InventoryResponse toInventoryResponse(InventoryItem item) { return new InventoryResponse(item.getId(), item.getWarehouse().getName(), item.getProduct().getSku(), item.getProduct().getName(), item.getQuantityAvailable(), item.getReorderPoint(), item.isLowStock()); }
    public MovementResponse toMovementResponse(InventoryMovement movement) { return new MovementResponse(movement.getId(), movement.getItem().getId(), movement.getType().name(), movement.getQuantityDelta(), movement.getNote(), movement.getItem().getQuantityAvailable()); }
}
