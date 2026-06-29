package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.Warehouse;
import java.util.List;

public interface WarehouseRepositoryPort {
    List<Warehouse> findAll();
    List<Warehouse> findByTenantIdOrderByIdAsc(Long tenantId);
    Warehouse save(Warehouse warehouse);
}
