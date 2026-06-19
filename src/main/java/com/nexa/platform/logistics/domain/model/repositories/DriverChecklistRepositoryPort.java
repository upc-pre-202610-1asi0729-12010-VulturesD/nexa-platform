package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.DriverChecklist;

public interface DriverChecklistRepositoryPort {
    DriverChecklist save(DriverChecklist checklist);
}
