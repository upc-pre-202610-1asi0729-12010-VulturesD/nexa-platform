package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantRepositoryPort {
    List<Tenant> findAllByOrderByIdAsc();
    Optional<Tenant> findById(Long id);
    Optional<Tenant> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByRuc(String ruc);
    Tenant save(Tenant tenant);
}
