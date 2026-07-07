package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.TenantCustomField;
import java.util.List;
import java.util.Optional;

public interface TenantCustomFieldRepositoryPort {
    List<TenantCustomField> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<TenantCustomField> findById(Long id);
    TenantCustomField save(TenantCustomField field);
    void delete(TenantCustomField field);
}
