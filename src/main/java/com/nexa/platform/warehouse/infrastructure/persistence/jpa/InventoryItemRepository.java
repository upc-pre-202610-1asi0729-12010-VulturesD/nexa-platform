package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.InventoryItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByQuantityAvailableLessThanEqual(int threshold);
}
