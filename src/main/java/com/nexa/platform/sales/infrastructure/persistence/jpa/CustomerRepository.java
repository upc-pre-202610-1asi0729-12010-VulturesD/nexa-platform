package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.Customer;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryPort {
    Optional<Customer> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Customer> findByTenantIdAndCode(Long tenantId, String code);
    Optional<Customer> findByTenantIdAndTaxId(Long tenantId, String taxId);
    List<Customer> findByTenantIdOrderByBusinessNameAsc(Long tenantId);
}
