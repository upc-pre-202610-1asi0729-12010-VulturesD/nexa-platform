package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.TenantMember;
import java.util.List;
import java.util.Optional;

public interface TenantMemberRepositoryPort {
    List<TenantMember> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<TenantMember> findById(Long id);
    TenantMember save(TenantMember member);
    void delete(TenantMember member);
}
