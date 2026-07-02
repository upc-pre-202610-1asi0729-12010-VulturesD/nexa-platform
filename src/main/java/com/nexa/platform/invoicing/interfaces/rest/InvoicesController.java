package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.ChangeInvoiceStatusResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.InvoiceResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.UpdateInvoiceResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.InvoiceResourceAssembler;
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
@RequestMapping("/api/v1/invoices")
@PreAuthorize("isAuthenticated()")
public class InvoicesController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;
    public InvoicesController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public ResponseEntity<InvoiceResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody InvoiceResource resource) {
        InvoiceResource created = InvoiceResourceAssembler.toResourceFromEntity(
            service.createInvoice(workspace.requireTenant(tenantId),
                InvoiceResourceAssembler.toRequestFromResource(resource)));
        return ResponseEntity.created(URI.create("/api/v1/invoices/" + created.id())).body(created);
    }
    @GetMapping public List<InvoiceResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listInvoices(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(InvoiceResourceAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public InvoiceResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return InvoiceResourceAssembler.toResourceFromEntity(
            service.getInvoice(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }
    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public InvoiceResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody UpdateInvoiceResource resource) {
        return InvoiceResourceAssembler.toResourceFromEntity(
            service.updateInvoice(workspace.requireTenant(tenantId), id,
                InvoiceResourceAssembler.toRequestFromResource(resource)));
    }
    @Operation(summary = "Change an unpaid invoice to paid or voided")
    @PostMapping("/{id}/status-changes")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public ResponseEntity<InvoiceResource> changeStatus(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody ChangeInvoiceStatusResource resource) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        String status = resource.paymentStatus().trim().toLowerCase();
        if ("paid".equals(status)) {
            return ResponseEntity.ok(InvoiceResourceAssembler.toResourceFromEntity(
                service.markInvoicePaid(scopedTenantId, id)));
        }
        if ("cancelled".equals(status) || "voided".equals(status)) {
            service.voidInvoice(scopedTenantId, id);
            return ResponseEntity.noContent().build();
        }
        throw new IllegalArgumentException("Only paid and cancelled invoice status changes are supported.");
    }
    @Operation(summary = "Void an unpaid invoice")
    @PostMapping("/{id}/voidings")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void voidInvoice(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        service.voidInvoice(workspace.requireTenant(tenantId), id);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        service.voidInvoice(workspace.requireTenant(tenantId), id);
    }
}
