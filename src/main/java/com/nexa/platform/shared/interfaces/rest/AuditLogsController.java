package com.nexa.platform.shared.interfaces.rest;

import com.nexa.platform.shared.application.auditing.AuditLogService;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
@PreAuthorize("isAuthenticated()")
public class AuditLogsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final AuditLogService service;
    private final CurrentWorkspaceContext workspace;

    public AuditLogsController(AuditLogService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<AuditLogService.AuditLogResponse> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @RequestParam(defaultValue = "100") int limit) {
        return service.list(workspace.requireTenant(tenantId), workspace.clientAccountId(), limit);
    }

    @GetMapping("/{id}")
    public AuditLogService.AuditLogResponse get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return service.get(workspace.requireTenant(tenantId), workspace.clientAccountId(), id);
    }
}
