package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.InventoryMovement;

public interface InventoryMovementRepositoryPort {
    InventoryMovement save(InventoryMovement movement);
}
