package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.InventoryMovement;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepositoryPort {
    List<InventoryMovement> findByTenantIdOrderByOccurredAtDescIdDesc(Long tenantId);
    Optional<InventoryMovement> findByTenantIdAndCode(Long tenantId, String code);
    InventoryMovement save(InventoryMovement movement);
}
