package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.InventoryItem;
import com.nexa.platform.warehouse.domain.model.repositories.InventoryItemRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, InventoryItemRepositoryPort {
    List<InventoryItem> findByQuantityAvailableLessThanEqual(int threshold);
    Optional<InventoryItem> findByIdAndTenantId(Long id, Long tenantId);
    List<InventoryItem> findByTenantIdOrderByIdAsc(Long tenantId);
}
