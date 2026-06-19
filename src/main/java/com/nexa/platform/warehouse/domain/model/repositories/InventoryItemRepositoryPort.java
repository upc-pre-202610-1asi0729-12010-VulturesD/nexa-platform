package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepositoryPort {
    Optional<InventoryItem> findById(Long id);
    List<InventoryItem> findAll();
    InventoryItem save(InventoryItem item);
}
