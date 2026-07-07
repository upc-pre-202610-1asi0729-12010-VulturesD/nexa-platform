package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.TenantCustomField;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantCustomFieldRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantCustomFieldRepository extends JpaRepository<TenantCustomField, Long>, TenantCustomFieldRepositoryPort {
    List<TenantCustomField> findByTenantIdOrderByIdAsc(Long tenantId);
}
