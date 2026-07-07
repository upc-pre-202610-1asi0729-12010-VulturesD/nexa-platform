package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.TenantSubscription;
import java.util.List;
import java.util.Optional;

public interface TenantSubscriptionRepositoryPort {
    List<TenantSubscription> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<TenantSubscription> findById(Long id);
    TenantSubscription save(TenantSubscription subscription);
    void delete(TenantSubscription subscription);
}
