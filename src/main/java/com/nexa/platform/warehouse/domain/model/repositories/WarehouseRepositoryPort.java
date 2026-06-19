package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.Warehouse;
import java.util.List;

public interface WarehouseRepositoryPort {
    List<Warehouse> findAll();
    Warehouse save(Warehouse warehouse);
}
