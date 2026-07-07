package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.Workspace;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepositoryPort {
    List<Workspace> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Workspace> findAllByOrderByIdAsc();
    Optional<Workspace> findById(Long id);
    Optional<Workspace> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Workspace save(Workspace workspace);
    void delete(Workspace workspace);
}
