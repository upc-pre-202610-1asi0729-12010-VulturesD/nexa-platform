package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.InventoryReservationRecord;
import java.util.List;
import java.util.Optional;

public interface InventoryReservationRepositoryPort {
    List<InventoryReservationRecord> findByTenantIdOrderByIdDesc(Long tenantId);
    Optional<InventoryReservationRecord> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByTenantIdAndInventoryItemIdAndCodeAndStatus(Long tenantId, Long inventoryItemId,
                                                               String code, String status);
    InventoryReservationRecord save(InventoryReservationRecord reservation);
}
