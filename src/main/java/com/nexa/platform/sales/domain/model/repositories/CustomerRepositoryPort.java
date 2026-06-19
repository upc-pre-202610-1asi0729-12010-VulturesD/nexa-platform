package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    Customer save(Customer customer);
}
