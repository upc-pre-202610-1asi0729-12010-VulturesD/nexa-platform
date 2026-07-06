package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.WorkspaceFeature;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceFeatureRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceFeatureRepository extends JpaRepository<WorkspaceFeature, Long>, WorkspaceFeatureRepositoryPort {
    List<WorkspaceFeature> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
}
