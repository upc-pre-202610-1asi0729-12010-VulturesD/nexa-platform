package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.Warehouse;
import com.nexa.platform.warehouse.domain.model.repositories.WarehouseRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, WarehouseRepositoryPort { }
