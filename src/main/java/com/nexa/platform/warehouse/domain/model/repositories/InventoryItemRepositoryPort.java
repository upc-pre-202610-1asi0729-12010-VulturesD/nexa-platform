package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepositoryPort {
    Optional<InventoryItem> findById(Long id);
    Optional<InventoryItem> findByIdAndTenantId(Long id, Long tenantId);
    List<InventoryItem> findAll();
    List<InventoryItem> findByTenantIdOrderByIdAsc(Long tenantId);
    InventoryItem save(InventoryItem item);
}
