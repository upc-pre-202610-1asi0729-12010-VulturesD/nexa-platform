package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.dtos.NotificationRecordDtos;
import com.nexa.platform.invoicing.application.internal.NotificationRecordService;
import com.nexa.platform.invoicing.interfaces.rest.resources.NotificationRecordResources;
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
@RequestMapping("/api/v1/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationRecordsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final NotificationRecordService service;
    private final CurrentWorkspaceContext workspace;

    public NotificationRecordsController(NotificationRecordService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<NotificationRecordResources.NotificationRecordResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.list(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(NotificationRecordsController::toResource).toList();
    }

    @GetMapping("/{id}")
    public NotificationRecordResources.NotificationRecordResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return toResource(service.get(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public ResponseEntity<NotificationRecordResources.NotificationRecordResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody NotificationRecordResources.UpsertNotificationRecordResource resource) {
        var created = toResource(service.create(workspace.requireTenant(tenantId), toRequest(resource)));
        return ResponseEntity.created(URI.create("/api/v1/notifications/" + created.id())).body(created);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public NotificationRecordResources.NotificationRecordResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id,
        @Valid @RequestBody NotificationRecordResources.UpsertNotificationRecordResource resource) {
        return toResource(service.update(workspace.requireTenant(tenantId), id, toRequest(resource)));
    }

    @Operation(summary = "Mark a scoped notification as read")
    @PostMapping("/{id}/reads")
    public NotificationRecordResources.NotificationRecordResource markRead(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return toResource(service.markRead(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public void delete(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        service.delete(workspace.requireTenant(tenantId), id);
    }

    private static NotificationRecordDtos.UpsertNotificationRecordRequest toRequest(
        NotificationRecordResources.UpsertNotificationRecordResource resource) {
        return new NotificationRecordDtos.UpsertNotificationRecordRequest(resource.clientAccountId(),
            resource.recipientRole(), resource.type(), resource.title(), resource.body(), resource.read());
    }

    private static NotificationRecordResources.NotificationRecordResource toResource(
        NotificationRecordDtos.NotificationRecordResponse response) {
        return new NotificationRecordResources.NotificationRecordResource(response.id(), response.tenantId(),
            response.clientAccountId(), response.recipientRole(), response.type(), response.title(),
            response.body(), response.read(), response.createdAt(), response.updatedAt());
    }
}
