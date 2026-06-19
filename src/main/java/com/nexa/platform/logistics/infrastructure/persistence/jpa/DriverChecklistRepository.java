package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.DriverChecklist;
import com.nexa.platform.logistics.domain.model.repositories.DriverChecklistRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverChecklistRepository extends JpaRepository<DriverChecklist, Long>, DriverChecklistRepositoryPort { }
