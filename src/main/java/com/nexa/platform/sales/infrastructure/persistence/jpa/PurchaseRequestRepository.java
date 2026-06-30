package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.PurchaseRequest;
import com.nexa.platform.sales.domain.model.repositories.PurchaseRequestRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long>, PurchaseRequestRepositoryPort {
    List<PurchaseRequest> findAllByOrderByIdAsc();
    List<PurchaseRequest> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<PurchaseRequest> findByCode(String code);
    Optional<PurchaseRequest> findByTenantIdAndCode(Long tenantId, String code);
    boolean existsByCode(String code);
}
