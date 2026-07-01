package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.dtos.PurchaseRequestWorkflowDtos;
import com.nexa.platform.sales.application.internal.PurchaseRequestWorkflowService;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestWorkflowResources;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversation-messages")
@PreAuthorize("isAuthenticated()")
public class ConversationMessagesController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final PurchaseRequestWorkflowService service;
    private final CurrentWorkspaceContext workspace;

    public ConversationMessagesController(PurchaseRequestWorkflowService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<PurchaseRequestWorkflowResources.ConversationMessageResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listMessages(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(ConversationMessagesController::toResource).toList();
    }

    @GetMapping("/{id}")
    public PurchaseRequestWorkflowResources.ConversationMessageResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return toResource(service.getMessage(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @Operation(summary = "Create a tenant-scoped conversation message")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestWorkflowResources.ConversationMessageResource create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody PurchaseRequestWorkflowResources.UpsertConversationMessageResource resource) {
        return toResource(service.createMessage(workspace.requireTenant(tenantId), toRequest(resource, workspace.clientAccountId())));
    }

    @PutMapping("/{id}")
    public PurchaseRequestWorkflowResources.ConversationMessageResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id,
        @Valid @RequestBody PurchaseRequestWorkflowResources.UpsertConversationMessageResource resource) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        service.getMessage(scopedTenantId, workspace.clientAccountId(), id);
        return toResource(service.updateMessage(scopedTenantId, id, toRequest(resource, workspace.clientAccountId())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        service.getMessage(scopedTenantId, workspace.clientAccountId(), id);
        service.deleteMessage(scopedTenantId, id);
    }

    static PurchaseRequestWorkflowResources.ConversationMessageResource toResource(
        PurchaseRequestWorkflowDtos.MessageResponse response) {
        return new PurchaseRequestWorkflowResources.ConversationMessageResource(
            response.id(), response.tenantId(), response.clientAccountId(), response.purchaseRequestId(),
            response.orderId(), response.senderRole(), response.senderName(), response.body(),
            response.visibleToBuyer(), response.createdAt(), response.updatedAt());
    }

    private static PurchaseRequestWorkflowDtos.MessageRequest toRequest(
        PurchaseRequestWorkflowResources.UpsertConversationMessageResource resource, Long authenticatedClientAccountId) {
        boolean buyer = authenticatedClientAccountId != null;
        return new PurchaseRequestWorkflowDtos.MessageRequest(
            buyer ? authenticatedClientAccountId : resource.clientAccountId(), resource.purchaseRequestId(),
            resource.orderId(), buyer ? "buyer" : resource.senderRole(), resource.senderName(), resource.body(),
            buyer ? true : resource.visibleToBuyer());
    }
}
