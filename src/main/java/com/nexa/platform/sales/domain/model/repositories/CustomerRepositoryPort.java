package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Customer> findByTenantIdAndCode(Long tenantId, String code);
    Optional<Customer> findByTenantIdAndTaxId(Long tenantId, String taxId);
    List<Customer> findAll();
    List<Customer> findByTenantIdOrderByBusinessNameAsc(Long tenantId);
    Customer save(Customer customer);
}
