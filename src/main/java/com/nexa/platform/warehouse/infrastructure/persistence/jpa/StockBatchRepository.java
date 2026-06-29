package com.nexa.platform.warehouse.infrastructure.persistence.jpa;

import com.nexa.platform.warehouse.domain.model.StockBatch;
import com.nexa.platform.warehouse.domain.model.repositories.StockBatchRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long>, StockBatchRepositoryPort {
    List<StockBatch> findByTenantIdOrderByExpirationDateAscIdAsc(Long tenantId);
}
