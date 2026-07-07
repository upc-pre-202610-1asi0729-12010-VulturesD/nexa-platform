package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.Workspace;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long>, WorkspaceRepositoryPort {
    List<Workspace> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Workspace> findAllByOrderByIdAsc();
    Optional<Workspace> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
