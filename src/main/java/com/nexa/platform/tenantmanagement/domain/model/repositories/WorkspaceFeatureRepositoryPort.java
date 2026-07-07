package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.WorkspaceFeature;
import java.util.List;
import java.util.Optional;

public interface WorkspaceFeatureRepositoryPort {
    List<WorkspaceFeature> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
    Optional<WorkspaceFeature> findById(Long id);
    WorkspaceFeature save(WorkspaceFeature feature);
    void delete(WorkspaceFeature feature);
}
