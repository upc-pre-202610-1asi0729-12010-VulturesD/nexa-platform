package com.nexa.platform.warehouse.domain.model.repositories;

import com.nexa.platform.warehouse.domain.model.StockBatch;
import java.util.List;
import java.util.Optional;

public interface StockBatchRepositoryPort {
    List<StockBatch> findByTenantIdOrderByExpirationDateAscIdAsc(Long tenantId);
    Optional<StockBatch> findByTenantIdAndLotCode(Long tenantId, String lotCode);
    Optional<StockBatch> findByIdAndTenantId(Long id, Long tenantId);
    StockBatch save(StockBatch lot);
}
