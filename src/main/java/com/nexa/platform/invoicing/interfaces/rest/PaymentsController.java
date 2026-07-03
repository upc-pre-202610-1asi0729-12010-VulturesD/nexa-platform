package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.RejectPaymentResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.PaymentResourceAssembler;
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
@RequestMapping("/api/v1/payments")
@PreAuthorize("isAuthenticated()")
public class PaymentsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;
    public PaymentsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }
    @GetMapping public List<PaymentResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        Long clientAccountId = workspace.clientAccountId();
        return service.listPayments(workspace.requireTenant(tenantId)).stream()
            .filter(payment -> clientAccountId == null || clientAccountId.equals(payment.clientAccountId()))
            .map(PaymentResourceAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}")
    public PaymentResource get(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
                               @PathVariable Long id) {
        var resource = PaymentResourceAssembler.toResourceFromEntity(
            service.getPayment(workspace.requireTenant(tenantId), id));
        requireClientAccess(resource.clientAccountId());
        return resource;
    }
    @Operation(summary = "Register a pending payment")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public ResponseEntity<PaymentResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody PaymentResource resource) {
        PaymentResource created = PaymentResourceAssembler.toResourceFromEntity(
            service.registerPayment(workspace.requireTenant(tenantId),
                PaymentResourceAssembler.toRequestFromResource(resource)));
        return ResponseEntity.created(URI.create("/api/v1/payments/" + created.backendId())).body(created);
    }
    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody PaymentResource resource) {
        return PaymentResourceAssembler.toResourceFromEntity(
            service.updatePayment(workspace.requireTenant(tenantId), id,
                PaymentResourceAssembler.toRequestFromResource(resource)));
    }
    @Operation(summary = "Confirm a pending payment")
    @PostMapping("/{id}/confirmations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentResource confirm(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return PaymentResourceAssembler.toResourceFromEntity(
            service.confirmPayment(workspace.requireTenant(tenantId), id));
    }
    @Operation(summary = "Reject a payment")
    @PostMapping("/{id}/rejections")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentResource reject(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id,
        @RequestBody(required = false) RejectPaymentResource resource) {
        return PaymentResourceAssembler.toResourceFromEntity(
            service.rejectPayment(workspace.requireTenant(tenantId), id, resource == null ? null : resource.reason()));
    }
    @Operation(summary = "Cancel an unconfirmed payment")
    @PostMapping("/{id}/cancellations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentResource cancel(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return PaymentResourceAssembler.toResourceFromEntity(
            service.cancelPayment(workspace.requireTenant(tenantId), id));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
                       @PathVariable Long id) {
        service.cancelPayment(workspace.requireTenant(tenantId), id);
    }

    private void requireClientAccess(Long requestedClientAccountId) {
        Long authenticatedClientAccountId = workspace.clientAccountId();
        if (authenticatedClientAccountId != null && !authenticatedClientAccountId.equals(requestedClientAccountId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        }
    }
}
