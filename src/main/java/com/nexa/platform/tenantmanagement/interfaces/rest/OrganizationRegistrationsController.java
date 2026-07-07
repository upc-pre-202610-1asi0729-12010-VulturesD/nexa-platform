package com.nexa.platform.tenantmanagement.interfaces.rest;

import com.nexa.platform.tenantmanagement.application.commandservices.TenantManagementCommandService;
import com.nexa.platform.tenantmanagement.application.queryservices.TenantManagementQueryService;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.CreateOrganizationRegistrationResource;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.OrganizationRegistrationResource;
import com.nexa.platform.tenantmanagement.interfaces.rest.transform.TenantManagementResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/organization-registrations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Organization Registrations", description = "Tenant onboarding registration endpoints")
public class OrganizationRegistrationsController {
    private final TenantManagementCommandService commandService;
    private final TenantManagementQueryService queryService;

    public OrganizationRegistrationsController(TenantManagementCommandService commandService, TenantManagementQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Operation(summary = "List organization registrations")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<OrganizationRegistrationResource> list() {
        return queryService.listOrganizationRegistrations().stream().map(TenantManagementResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get organization registration by external ID")
    @GetMapping("/{externalId}")
    @PreAuthorize("isAuthenticated()")
    public OrganizationRegistrationResource get(@PathVariable String externalId) {
        return TenantManagementResourceAssembler.toResource(queryService.getOrganizationRegistration(externalId));
    }

    @Operation(summary = "Create organization registration")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationRegistrationResource create(@Valid @RequestBody CreateOrganizationRegistrationResource resource) {
        return TenantManagementResourceAssembler.toResource(
            commandService.handle(TenantManagementResourceAssembler.toCommand(resource)));
    }
}
