package com.nexa.platform.tenantmanagement.interfaces.rest;

import com.nexa.platform.tenantmanagement.application.commandservices.TenantManagementCommandService;
import com.nexa.platform.tenantmanagement.application.queryservices.TenantManagementQueryService;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.TenantPreviewResource;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.TenantResource;
import com.nexa.platform.tenantmanagement.interfaces.rest.resources.TenantManagementResources.UpsertTenantResource;
import com.nexa.platform.tenantmanagement.interfaces.rest.transform.TenantManagementResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/tenants", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Tenants", description = "Tenant and workspace preview endpoints")
public class TenantsController {
    private final TenantManagementCommandService commandService;
    private final TenantManagementQueryService queryService;
    private final CurrentWorkspaceContext workspace;

    public TenantsController(TenantManagementCommandService commandService, TenantManagementQueryService queryService,
                             CurrentWorkspaceContext workspace) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.workspace = workspace;
    }

    @Operation(summary = "List tenants")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TenantResource> list(@RequestHeader(value = "X-Nexa-Tenant-Id", required = false) Long tenantId) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        return List.of(TenantManagementResourceAssembler.toResource(queryService.getTenant(scopedTenantId)));
    }

    @Operation(summary = "Get tenant by ID")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TenantResource get(@PathVariable Long id) {
        workspace.requireTenant(id);
        return TenantManagementResourceAssembler.toResource(queryService.getTenant(id));
    }

    @Operation(summary = "Resolve tenant preview by workspace slug")
    @GetMapping("/preview/{slug}")
    public TenantPreviewResource preview(@PathVariable String slug) {
        return TenantManagementResourceAssembler.toPreviewResource(queryService.getTenantBySlug(slug));
    }

    @Operation(summary = "Create tenant")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TenantResource create(@Valid @RequestBody UpsertTenantResource resource) {
        return TenantManagementResourceAssembler.toResource(
            commandService.handle(TenantManagementResourceAssembler.toCommand(null, resource)));
    }

    @Operation(summary = "Update tenant")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TenantResource update(@PathVariable Long id, @Valid @RequestBody UpsertTenantResource resource) {
        workspace.requireTenant(id);
        return TenantManagementResourceAssembler.toResource(
            commandService.handle(TenantManagementResourceAssembler.toCommand(id, resource)));
    }
}
