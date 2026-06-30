package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.interfaces.rest.resources.CustomerResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertCustomerResource;
import com.nexa.platform.sales.interfaces.rest.transform.CustomerResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/client-account")
@PreAuthorize("isAuthenticated()")
public class BuyerClientProfileController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final SalesService service;
    private final CurrentWorkspaceContext workspace;

    public BuyerClientProfileController(SalesService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public CustomerResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        Long clientAccountId = requireClientAccount();
        return CustomerResourceFromEntityAssembler.toResourceFromEntity(
            service.getCustomer(workspace.requireTenant(tenantId), clientAccountId, clientAccountId));
    }

    @PutMapping
    public CustomerResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody UpsertCustomerResource resource) {
        Long clientAccountId = requireClientAccount();
        return CustomerResourceFromEntityAssembler.toResourceFromEntity(
            service.updateCustomer(workspace.requireTenant(tenantId), clientAccountId, clientAccountId,
                CustomerResourceFromEntityAssembler.toRequestFromResource(resource)));
    }

    private Long requireClientAccount() {
        Long clientAccountId = workspace.clientAccountId();
        if (clientAccountId == null || clientAccountId <= 0) {
            throw new IllegalStateException("Buyer client account membership is required.");
        }
        return clientAccountId;
    }
}
