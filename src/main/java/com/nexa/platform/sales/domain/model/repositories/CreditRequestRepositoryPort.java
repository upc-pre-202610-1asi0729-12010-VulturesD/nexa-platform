package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.CreditRequest;
import java.util.List;
import java.util.Optional;

public interface CreditRequestRepositoryPort {
    List<CreditRequest> findByTenantIdOrderByIdDesc(Long tenantId);
    List<CreditRequest> findByTenantIdAndClientAccountIdOrderByIdDesc(Long tenantId, Long clientAccountId);
    Optional<CreditRequest> findByIdAndTenantId(Long id, Long tenantId);
    Optional<CreditRequest> findByIdAndTenantIdAndClientAccountId(Long id, Long tenantId, Long clientAccountId);
    Optional<CreditRequest> findByTenantIdAndCode(Long tenantId, String code);
    CreditRequest save(CreditRequest request);
    void delete(CreditRequest request);
}
