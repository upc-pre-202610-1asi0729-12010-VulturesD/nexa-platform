package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.InventoryMovement;
import com.nexa.platform.warehouse.domain.model.repositories.InventoryMovementRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>, InventoryMovementRepositoryPort { }
