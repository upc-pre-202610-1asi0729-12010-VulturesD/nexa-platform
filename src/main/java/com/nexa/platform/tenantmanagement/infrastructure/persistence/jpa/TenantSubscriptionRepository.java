package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.TenantSubscription;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantSubscriptionRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantSubscriptionRepository extends JpaRepository<TenantSubscription, Long>, TenantSubscriptionRepositoryPort {
    List<TenantSubscription> findByTenantIdOrderByIdAsc(Long tenantId);
}
