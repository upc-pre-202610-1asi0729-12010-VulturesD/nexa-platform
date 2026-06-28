package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.Warehouse;
import com.nexa.platform.warehouse.domain.model.repositories.WarehouseRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, WarehouseRepositoryPort {
    List<Warehouse> findByTenantIdOrderByIdAsc(Long tenantId);
}
