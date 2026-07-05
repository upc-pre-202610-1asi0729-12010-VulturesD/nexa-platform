package com.nexa.platform.logistics.infrastructure.persistence.jpa;

import com.nexa.platform.logistics.domain.model.DispatchOrder;
import com.nexa.platform.logistics.domain.model.repositories.DispatchOrderRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchOrderRepository extends JpaRepository<DispatchOrder, Long>, DispatchOrderRepositoryPort {
    List<DispatchOrder> findAllByOrderByIdAsc();
    Optional<DispatchOrder> findByCode(String code);
    Optional<DispatchOrder> findByOrderId(Long orderId);
    Optional<DispatchOrder> findByTenantIdAndOrderId(Long tenantId, Long orderId);
    boolean existsByCode(String code);
}
