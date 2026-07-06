package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.OrganizationRegistrationRequest;
import com.nexa.platform.tenantmanagement.domain.model.repositories.OrganizationRegistrationRequestRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRegistrationRequestRepository extends JpaRepository<OrganizationRegistrationRequest, Long>, OrganizationRegistrationRequestRepositoryPort {
    List<OrganizationRegistrationRequest> findAllByOrderByIdDesc();
    Optional<OrganizationRegistrationRequest> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
}
