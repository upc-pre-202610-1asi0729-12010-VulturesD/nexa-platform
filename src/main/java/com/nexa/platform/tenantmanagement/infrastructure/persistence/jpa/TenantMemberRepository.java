package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.TenantMember;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantMemberRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMemberRepository extends JpaRepository<TenantMember, Long>, TenantMemberRepositoryPort {
    List<TenantMember> findByTenantIdOrderByIdAsc(Long tenantId);
}
