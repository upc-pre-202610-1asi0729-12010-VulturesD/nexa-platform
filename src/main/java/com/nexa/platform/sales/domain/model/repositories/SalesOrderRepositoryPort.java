package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.SalesOrder;
import java.util.List;
import java.util.Optional;

public interface SalesOrderRepositoryPort {
    Optional<SalesOrder> findById(Long id);
    Optional<SalesOrder> findByIdAndTenantId(Long id, Long tenantId);
    Optional<SalesOrder> findWithItemsById(Long id);
    Optional<SalesOrder> findWithItemsByIdAndTenantId(Long id, Long tenantId);
    List<SalesOrder> findAll();
    List<SalesOrder> findByTenantIdOrderByIdAsc(Long tenantId);
    SalesOrder save(SalesOrder order);
}
