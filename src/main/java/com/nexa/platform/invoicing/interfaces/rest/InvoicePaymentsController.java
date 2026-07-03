package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.PaymentResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices/{invoiceId}/payments")
@PreAuthorize("isAuthenticated()")
public class InvoicePaymentsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;

    public InvoicePaymentsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<PaymentResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long invoiceId) {
        return service.listPaymentsByInvoice(workspace.requireTenant(tenantId), invoiceId).stream()
            .map(PaymentResourceAssembler::toResourceFromEntity).toList();
    }
}
