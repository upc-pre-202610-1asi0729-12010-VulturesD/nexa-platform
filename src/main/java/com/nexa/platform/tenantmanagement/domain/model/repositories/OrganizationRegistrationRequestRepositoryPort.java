package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.OrganizationRegistrationRequest;
import java.util.List;
import java.util.Optional;

public interface OrganizationRegistrationRequestRepositoryPort {
    List<OrganizationRegistrationRequest> findAllByOrderByIdDesc();
    Optional<OrganizationRegistrationRequest> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    OrganizationRegistrationRequest save(OrganizationRegistrationRequest request);
}
