package com.nexa.platform.logistics.domain.model.repositories;

import com.nexa.platform.logistics.domain.model.DispatchOrder;
import java.util.List;
import java.util.Optional;

public interface DispatchOrderRepositoryPort {
    List<DispatchOrder> findAllByOrderByIdAsc();
    Optional<DispatchOrder> findById(Long id);
    Optional<DispatchOrder> findByCode(String code);
    Optional<DispatchOrder> findByOrderId(Long orderId);
    Optional<DispatchOrder> findByTenantIdAndOrderId(Long tenantId, Long orderId);
    boolean existsByCode(String code);
    DispatchOrder save(DispatchOrder dispatchOrder);
    void delete(DispatchOrder dispatchOrder);
}
