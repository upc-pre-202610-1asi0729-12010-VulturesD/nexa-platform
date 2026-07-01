package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long>, SalesOrderRepositoryPort {
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    @Query("select salesOrder from SalesOrder salesOrder where salesOrder.id = :id")
    Optional<SalesOrder> findWithItemsById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    @Query("select salesOrder from SalesOrder salesOrder where salesOrder.id = :id and salesOrder.tenantId = :tenantId")
    Optional<SalesOrder> findWithItemsByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);

    List<SalesOrder> findByTenantIdOrderByIdAsc(Long tenantId);
}
