package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.dtos.PurchaseRequestWorkflowDtos;
import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.application.internal.PurchaseRequestWorkflowService;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestResource;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestWorkflowResources;
import com.nexa.platform.sales.interfaces.rest.resources.RequestNoteResource;
import com.nexa.platform.sales.interfaces.rest.resources.RequestOwnerResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertPurchaseRequestResource;
import com.nexa.platform.sales.interfaces.rest.transform.PurchaseRequestResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping({"/api/v1/purchase-requests", "/api/v1/requests"})
@PreAuthorize("isAuthenticated()")
public class PurchaseRequestsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final SalesService service;
    private final PurchaseRequestWorkflowService workflow;
    private final CurrentWorkspaceContext workspace;

    public PurchaseRequestsController(SalesService service, PurchaseRequestWorkflowService workflow,
                                      CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workflow = workflow;
        this.workspace = workspace;
    }

    @GetMapping public List<PurchaseRequestResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listPurchaseRequests(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(PurchaseRequestResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public PurchaseRequestResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id) {
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.getPurchaseRequest(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @Operation(summary = "Create purchase request")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestResource create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody UpsertPurchaseRequestResource resource) {
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.createPurchaseRequest(workspace.requireTenant(tenantId), workspace.clientAccountId(), resource));
    }

    @Operation(summary = "Create manual purchase request")
    @PostMapping("/manual-creations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestResource createManual(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody UpsertPurchaseRequestResource resource) {
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.createPurchaseRequest(workspace.requireTenant(tenantId), null, resource));
    }

    @Operation(summary = "Update purchase request")
    @PutMapping("/{id}")
    public PurchaseRequestResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @Valid @RequestBody UpsertPurchaseRequestResource resource) {
        scopeRequest(tenantId, id);
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.updatePurchaseRequest(workspace.requireTenant(tenantId), workspace.clientAccountId(), id, resource));
    }

    @Operation(summary = "Delete purchase request")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id) {
        scopeRequest(tenantId, id);
        service.deletePurchaseRequest(workspace.requireTenant(tenantId), id);
    }

    @Operation(summary = "Submit purchase request")
    @PostMapping("/{id}/submissions")
    public PurchaseRequestResource submit(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @RequestBody(required = false) RequestNoteResource resource) {
        scopeRequest(tenantId, id);
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.submitPurchaseRequest(workspace.requireTenant(tenantId), id, note(resource)));
    }

    @Operation(summary = "Request buyer adjustment for a purchase request")
    @PostMapping("/{id}/adjustment-requests")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PurchaseRequestResource requestAdjustment(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @Valid @RequestBody RequestNoteResource resource) {
        requireNote(resource, "Adjustment reason is required.");
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.requestPurchaseRequestAdjustment(workspace.requireTenant(tenantId), id, resource.note()));
    }

    @Operation(summary = "Reject purchase request")
    @PostMapping("/{id}/rejections")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PurchaseRequestResource reject(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @Valid @RequestBody RequestNoteResource resource) {
        requireNote(resource, "Rejection reason is required.");
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.rejectPurchaseRequest(workspace.requireTenant(tenantId), id, resource.note()));
    }

    @Operation(summary = "Validate purchase request commercially")
    @PostMapping("/{id}/commercial-validations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PurchaseRequestResource validateCommercially(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @Valid @RequestBody RequestOwnerResource resource) {
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.validatePurchaseRequestCommercially(workspace.requireTenant(tenantId), id,
                resource.commercialOwner(), resource.comments()));
    }

    @Operation(summary = "Accept a commercially validated purchase request into an order")
    @PostMapping("/{id}/acceptances")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public PurchaseRequestWorkflowResources.OrderAcceptanceResource accept(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id,
        @RequestBody(required = false) RequestNoteResource resource) {
        var response = workflow.accept(workspace.requireTenant(tenantId), id, note(resource));
        return new PurchaseRequestWorkflowResources.OrderAcceptanceResource(
            response.purchaseRequestId(), response.orderId(), response.dispatchOrderId(), response.status());
    }

    @Operation(summary = "Create a stock reservation for a purchase request")
    @PostMapping("/{id}/reservations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestWorkflowResources.ReservationResource reserve(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id,
        @Valid @RequestBody PurchaseRequestWorkflowResources.ReservationRequestResource resource) {
        var response = workflow.reserve(workspace.requireTenant(tenantId), id, new PurchaseRequestWorkflowDtos.ReservationRequest(
            resource.id(), resource.inventoryItemId(), resource.productId(), resource.lotCode(), resource.units()));
        return new PurchaseRequestWorkflowResources.ReservationResource(
            response.id(), response.externalId(), response.status());
    }

    @Operation(summary = "Create a buyer or commercial message for a purchase request")
    @PostMapping({"/{id}/buyer-responses", "/{id}/messages"})
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestWorkflowResources.ConversationMessageResource createMessage(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id,
        @Valid @RequestBody PurchaseRequestWorkflowResources.PurchaseRequestMessageResource resource) {
        scopeRequest(tenantId, id);
        boolean buyer = workspace.clientAccountId() != null;
        var response = workflow.createRequestMessage(workspace.requireTenant(tenantId), id, new PurchaseRequestWorkflowDtos.MessageRequest(
            null, null, null, buyer ? "buyer" : resource.senderRole(), resource.senderName(), resource.body(),
            buyer ? true : resource.visibleToBuyer()));
        return ConversationMessagesController.toResource(response);
    }

    @Operation(summary = "Cancel purchase request")
    @PostMapping("/{id}/cancellations")
    public PurchaseRequestResource cancel(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable String id, @RequestBody(required = false) RequestNoteResource resource) {
        scopeRequest(tenantId, id);
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(
            service.cancelPurchaseRequest(workspace.requireTenant(tenantId), id, note(resource)));
    }

    private static String note(RequestNoteResource resource) {
        return resource == null ? null : resource.note();
    }

    private static void requireNote(RequestNoteResource resource, String message) {
        if (resource == null || resource.note() == null || resource.note().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void scopeRequest(Long requestedTenantId, String id) {
        service.getPurchaseRequest(workspace.requireTenant(requestedTenantId), workspace.clientAccountId(), id);
    }
}
