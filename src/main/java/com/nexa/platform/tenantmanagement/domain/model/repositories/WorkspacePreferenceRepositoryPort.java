package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.WorkspacePreference;
import java.util.List;
import java.util.Optional;

public interface WorkspacePreferenceRepositoryPort {
    List<WorkspacePreference> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
    Optional<WorkspacePreference> findById(Long id);
    WorkspacePreference save(WorkspacePreference preference);
    void delete(WorkspacePreference preference);
}
