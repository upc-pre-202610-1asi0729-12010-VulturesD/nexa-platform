package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.dtos.CreditRequestDtos;
import com.nexa.platform.sales.application.internal.CreditRequestService;
import com.nexa.platform.sales.interfaces.rest.resources.CreditRequestResources;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit-requests")
@PreAuthorize("isAuthenticated()")
public class CreditRequestsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final CreditRequestService service;
    private final CurrentWorkspaceContext workspace;

    public CreditRequestsController(CreditRequestService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<CreditRequestResources.CreditRequestResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.list(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(CreditRequestsController::toResource).toList();
    }

    @GetMapping("/{id}")
    public CreditRequestResources.CreditRequestResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return toResource(service.get(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @PostMapping
    public ResponseEntity<CreditRequestResources.CreditRequestResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody CreditRequestResources.CreateCreditRequestResource resource) {
        var created = toResource(service.create(workspace.requireTenant(tenantId), workspace.clientAccountId(),
            workspace.userId(), new CreditRequestDtos.CreateCreditRequestRequest(resource.clientAccountId(),
                resource.code(), resource.requestedAmount(), resource.reason())));
        return ResponseEntity.created(URI.create("/api/v1/credit-requests/" + created.id())).body(created);
    }

    @Operation(summary = "Approve, reject or cancel a submitted credit request")
    @PostMapping("/{id}/resolutions")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public CreditRequestResources.CreditRequestResource resolve(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id,
        @Valid @RequestBody CreditRequestResources.ResolveCreditRequestResource resource) {
        return toResource(service.resolve(workspace.requireTenant(tenantId), id,
            new CreditRequestDtos.ResolveCreditRequestRequest(
                resource.status(), resource.reviewedBy(), resource.note())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        service.delete(workspace.requireTenant(tenantId), id);
    }

    private static CreditRequestResources.CreditRequestResource toResource(
        CreditRequestDtos.CreditRequestResponse response) {
        return new CreditRequestResources.CreditRequestResource(response.id(), response.tenantId(),
            response.clientAccountId(), response.code(), response.requestedAmount(), response.reason(),
            response.status(), response.createdByUserId(), response.reviewedBy(), response.resolutionNote(),
            response.createdAt(), response.updatedAt());
    }
}
