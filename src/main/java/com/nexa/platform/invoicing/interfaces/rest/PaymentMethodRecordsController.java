package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentMethodRecordResources.*;
import com.nexa.platform.invoicing.interfaces.rest.transform.PaymentMethodRecordResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/payment-method-records", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payment Method Records", description = "Tenant-scoped buyer payment method references")
@PreAuthorize("isAuthenticated()")
public class PaymentMethodRecordsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;

    public PaymentMethodRecordsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List payment method records")
    @GetMapping
    public List<PaymentMethodRecordResource> list(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        Long clientAccountId = workspace.clientAccountId();
        return service.listPaymentMethods(workspace.requireTenant(tenantId)).stream()
            .map(PaymentMethodRecordResourceAssembler::toResource)
            .filter(resource -> clientAccountId == null || clientAccountId.equals(resource.clientAccountId()))
            .toList();
    }

    @Operation(summary = "Get payment method record")
    @GetMapping("/{id}")
    public PaymentMethodRecordResource get(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        var resource = PaymentMethodRecordResourceAssembler.toResource(
            service.getPaymentMethod(workspace.requireTenant(tenantId), id));
        requireClientAccess(resource.clientAccountId());
        return resource;
    }

    @Operation(summary = "Create payment method record")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentMethodRecordResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody CreatePaymentMethodRecordResource resource) {
        requireClientAccess(resource.clientAccountId());
        var created = PaymentMethodRecordResourceAssembler.toResource(
            service.createPaymentMethod(workspace.requireTenant(tenantId), PaymentMethodRecordResourceAssembler.toRequest(resource)));
        return ResponseEntity.created(URI.create("/api/v1/payment-method-records/" + created.id())).body(created);
    }

    @Operation(summary = "Change payment method status or default selection")
    @PostMapping(value = "/{id}/status-changes", consumes = APPLICATION_JSON_VALUE)
    public PaymentMethodRecordResource changeStatus(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id,
        @Valid @RequestBody ChangePaymentMethodRecordStatusResource resource) {
        var existing = PaymentMethodRecordResourceAssembler.toResource(
            service.getPaymentMethod(workspace.requireTenant(tenantId), id));
        requireClientAccess(existing.clientAccountId());
        return PaymentMethodRecordResourceAssembler.toResource(
            service.changePaymentMethodStatus(workspace.requireTenant(tenantId), id, PaymentMethodRecordResourceAssembler.toRequest(resource)));
    }

    private void requireClientAccess(Long requestedClientAccountId) {
        Long authenticatedClientAccountId = workspace.clientAccountId();
        if (authenticatedClientAccountId != null && !authenticatedClientAccountId.equals(requestedClientAccountId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        }
    }
}
