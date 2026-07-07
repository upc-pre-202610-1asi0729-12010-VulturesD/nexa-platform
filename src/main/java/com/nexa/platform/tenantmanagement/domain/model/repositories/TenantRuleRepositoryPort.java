package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.TenantRule;
import java.util.List;
import java.util.Optional;

public interface TenantRuleRepositoryPort {
    List<TenantRule> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<TenantRule> findById(Long id);
    TenantRule save(TenantRule rule);
    void delete(TenantRule rule);
}
