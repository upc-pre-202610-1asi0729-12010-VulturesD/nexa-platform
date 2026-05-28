package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> { }
