package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.ChangePaymentProcessStatusResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.CreatePaymentProcessRecordResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.PaymentProcessRecordResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.PaymentProcessRecordResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/payment-process-records", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Payment Process Records", description = "Commercial payment process workflow records")
@PreAuthorize("isAuthenticated()")
public class PaymentProcessRecordsController {
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;

    public PaymentProcessRecordsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List payment process records")
    @GetMapping
    public List<PaymentProcessRecordResource> list() {
        Long tenantId = workspace.requireTenant(null);
        Long clientAccountId = workspace.clientAccountId();
        return service.listPaymentProcesses(tenantId).stream().map(PaymentProcessRecordResourceAssembler::toResource)
            .filter(resource -> clientAccountId == null || clientAccountId.equals(resource.clientAccountId())).toList();
    }

    @Operation(summary = "Get payment process record")
    @GetMapping("/{id}")
    public PaymentProcessRecordResource get(@PathVariable Long id) {
        var resource = PaymentProcessRecordResourceAssembler.toResource(
            service.getPaymentProcess(workspace.requireTenant(null), id));
        requireClientAccess(resource);
        return resource;
    }

    @Operation(summary = "Create payment process record")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentProcessRecordResource create(@Valid @RequestBody CreatePaymentProcessRecordResource resource) {
        Long tenantId = workspace.requireTenant(resource.tenantId());
        return PaymentProcessRecordResourceAssembler.toResource(
            service.createPaymentProcess(tenantId, PaymentProcessRecordResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Change payment process status")
    @PostMapping("/{id}/status-changes")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentProcessRecordResource changeStatus(@PathVariable Long id,
                                                     @Valid @RequestBody ChangePaymentProcessStatusResource resource) {
        return PaymentProcessRecordResourceAssembler.toResource(
            service.changePaymentProcessStatus(workspace.requireTenant(null), id,
                PaymentProcessRecordResourceAssembler.toRequest(resource).status()));
    }

    @Operation(summary = "Confirm payment process")
    @PostMapping("/{id}/confirmations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentProcessRecordResource confirm(@PathVariable Long id) {
        return PaymentProcessRecordResourceAssembler.toResource(
            service.changePaymentProcessStatus(workspace.requireTenant(null), id, "confirmed"));
    }

    @Operation(summary = "Reject payment process")
    @PostMapping("/{id}/rejections")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public PaymentProcessRecordResource reject(@PathVariable Long id) {
        return PaymentProcessRecordResourceAssembler.toResource(
            service.changePaymentProcessStatus(workspace.requireTenant(null), id, "failed"));
    }

    private void requireClientAccess(PaymentProcessRecordResource resource) {
        Long clientAccountId = workspace.clientAccountId();
        if (clientAccountId != null && !clientAccountId.equals(resource.clientAccountId())) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
