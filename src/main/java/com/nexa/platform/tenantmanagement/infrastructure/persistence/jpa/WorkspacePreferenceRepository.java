package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.WorkspacePreference;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspacePreferenceRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspacePreferenceRepository extends JpaRepository<WorkspacePreference, Long>, WorkspacePreferenceRepositoryPort {
    List<WorkspacePreference> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
}
