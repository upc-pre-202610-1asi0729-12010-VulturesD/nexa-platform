package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.PurchaseRequest;
import java.util.List;
import java.util.Optional;

public interface PurchaseRequestRepositoryPort {
    List<PurchaseRequest> findAllByOrderByIdAsc();
    List<PurchaseRequest> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<PurchaseRequest> findById(Long id);
    Optional<PurchaseRequest> findByIdAndTenantId(Long id, Long tenantId);
    Optional<PurchaseRequest> findByCode(String code);
    Optional<PurchaseRequest> findByTenantIdAndCode(Long tenantId, String code);
    boolean existsByCode(String code);
    PurchaseRequest save(PurchaseRequest request);
    void delete(PurchaseRequest request);
}
