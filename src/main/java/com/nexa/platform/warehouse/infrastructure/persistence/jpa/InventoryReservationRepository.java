package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.InventoryReservationRecord;
import com.nexa.platform.warehouse.domain.model.repositories.InventoryReservationRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository
    extends JpaRepository<InventoryReservationRecord, Long>, InventoryReservationRepositoryPort {
    List<InventoryReservationRecord> findByTenantIdOrderByIdDesc(Long tenantId);
}
