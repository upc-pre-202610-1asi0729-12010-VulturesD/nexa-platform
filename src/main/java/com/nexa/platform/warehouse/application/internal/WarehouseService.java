package com.nexa.platform.warehouse.application.internal;

import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.warehouse.application.dtos.*;
import com.nexa.platform.warehouse.domain.model.*;
import com.nexa.platform.warehouse.infrastructure.persistence.jpa.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouses;
    private final InventoryItemRepository inventory;
    private final InventoryMovementRepository movements;
    private final WarehouseMapper mapper;
    public WarehouseService(WarehouseRepository warehouses, InventoryItemRepository inventory, InventoryMovementRepository movements, WarehouseMapper mapper) { this.warehouses = warehouses; this.inventory = inventory; this.movements = movements; this.mapper = mapper; }
    public List<WarehouseResponse> listWarehouses() { return warehouses.findAll().stream().map(mapper::toWarehouseResponse).toList(); }
    public List<InventoryResponse> listInventory() { return inventory.findAll().stream().map(mapper::toInventoryResponse).toList(); }
    public List<InventoryResponse> listAlerts() { return inventory.findAll().stream().filter(InventoryItem::isLowStock).map(mapper::toInventoryResponse).toList(); }
    @Transactional
    public MovementResponse registerMovement(MovementRequest request) {
        InventoryItem item = inventory.findById(request.inventoryItemId()).orElseThrow(() -> new ResourceNotFoundException("Inventory item", request.inventoryItemId()));
        MovementType type = MovementType.valueOf(request.type());
        item.apply(request.quantityDelta());
        return mapper.toMovementResponse(movements.save(new InventoryMovement(item, type, request.quantityDelta(), request.note())));
    }
}
