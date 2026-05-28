package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> { }
