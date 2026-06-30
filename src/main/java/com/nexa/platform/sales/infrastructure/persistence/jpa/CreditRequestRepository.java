package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.CreditRequest;
import com.nexa.platform.sales.domain.model.repositories.CreditRequestRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long>, CreditRequestRepositoryPort {
    List<CreditRequest> findByTenantIdOrderByIdDesc(Long tenantId);
    List<CreditRequest> findByTenantIdAndClientAccountIdOrderByIdDesc(Long tenantId, Long clientAccountId);
    Optional<CreditRequest> findByIdAndTenantId(Long id, Long tenantId);
    Optional<CreditRequest> findByIdAndTenantIdAndClientAccountId(Long id, Long tenantId, Long clientAccountId);
    Optional<CreditRequest> findByTenantIdAndCode(Long tenantId, String code);
}
