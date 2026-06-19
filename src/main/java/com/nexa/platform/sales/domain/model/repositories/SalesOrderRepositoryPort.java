package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.SalesOrder;
import java.util.List;
import java.util.Optional;

public interface SalesOrderRepositoryPort {
    Optional<SalesOrder> findById(Long id);
    List<SalesOrder> findAll();
    SalesOrder save(SalesOrder order);
}
