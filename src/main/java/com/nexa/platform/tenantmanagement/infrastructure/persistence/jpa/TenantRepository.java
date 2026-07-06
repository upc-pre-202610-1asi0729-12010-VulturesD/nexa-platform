package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.Tenant;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long>, TenantRepositoryPort {
    List<Tenant> findAllByOrderByIdAsc();
    Optional<Tenant> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByRuc(String ruc);
}
