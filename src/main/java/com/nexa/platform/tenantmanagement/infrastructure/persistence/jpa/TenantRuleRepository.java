package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.TenantRule;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantRuleRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRuleRepository extends JpaRepository<TenantRule, Long>, TenantRuleRepositoryPort {
    List<TenantRule> findByTenantIdOrderByIdAsc(Long tenantId);
}
