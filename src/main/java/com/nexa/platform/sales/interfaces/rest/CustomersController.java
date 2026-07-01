package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.dtos.CustomerResponse;
import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.interfaces.rest.resources.ClientFinancialProfileResource;
import com.nexa.platform.sales.interfaces.rest.resources.CustomerResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertCustomerResource;
import com.nexa.platform.sales.interfaces.rest.transform.CustomerResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/customers", "/api/v1/client-accounts"})
@PreAuthorize("isAuthenticated()")
public class CustomersController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final SalesService service;
    private final CurrentWorkspaceContext workspace;

    public CustomersController(SalesService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<CustomerResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @RequestParam(required = false) String code) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        if (code != null && !code.isBlank()) {
            return List.of(CustomerResourceFromEntityAssembler.toResourceFromEntity(
                service.getCustomerByCode(scopedTenantId, workspace.clientAccountId(), code)));
        }
        return service.listCustomers(scopedTenantId, workspace.clientAccountId()).stream()
            .map(CustomerResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public CustomerResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return CustomerResourceFromEntityAssembler.toResourceFromEntity(
            service.getCustomer(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @Operation(summary = "Get the client credit and payment profile")
    @GetMapping("/{id}/financial-profile")
    public ClientFinancialProfileResource financialProfile(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        CustomerResponse client = service.getCustomer(
            workspace.requireTenant(tenantId), workspace.clientAccountId(), id);
        return new ClientFinancialProfileResource(client.id(), client.tenantId(), client.code(),
            client.paymentCondition(), client.monthlyCreditLimit(), client.monthlyCreditUsed(),
            client.monthlyCreditAvailable(), client.monthlyCreditStatus(), client.status());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public ResponseEntity<CustomerResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody UpsertCustomerResource resource) {
        if (workspace.clientAccountId() != null) {
            throw new IllegalStateException("Buyer memberships cannot create client accounts.");
        }
        CustomerResource created = CustomerResourceFromEntityAssembler.toResourceFromEntity(
            service.createCustomer(workspace.requireTenant(tenantId),
                CustomerResourceFromEntityAssembler.toRequestFromResource(resource)));
        return ResponseEntity.created(URI.create("/api/v1/client-accounts/" + created.id())).body(created);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public CustomerResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody UpsertCustomerResource resource) {
        return CustomerResourceFromEntityAssembler.toResourceFromEntity(
            service.updateCustomer(workspace.requireTenant(tenantId), workspace.clientAccountId(), id,
                CustomerResourceFromEntityAssembler.toRequestFromResource(resource)));
    }
}
