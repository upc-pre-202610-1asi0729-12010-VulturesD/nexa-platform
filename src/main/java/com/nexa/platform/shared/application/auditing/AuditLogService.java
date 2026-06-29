package com.nexa.platform.shared.application.auditing;

import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import com.nexa.platform.shared.domain.model.AuditLog;
import com.nexa.platform.shared.domain.repositories.AuditLogRepositoryPort;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditLogService {
    private final AuditLogRepositoryPort auditLogs;
    private final CurrentWorkspaceContext workspace;

    public AuditLogService(AuditLogRepositoryPort auditLogs, CurrentWorkspaceContext workspace) {
        this.auditLogs = auditLogs;
        this.workspace = workspace;
    }

    @Transactional
    public void record(Long tenantId, String action, String resourceType, Object resourceId, String metadataJson) {
        HttpServletRequest request = currentRequest();
        auditLogs.save(new AuditLog(tenantId, workspace.workspaceId(), workspace.userId(), workspace.membershipId(),
            action, resourceType, String.valueOf(resourceId), metadataJson,
            request == null ? null : request.getRemoteAddr(),
            request == null ? null : request.getHeader("User-Agent")));
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> list(Long tenantId, Long clientAccountId, int limit) {
        if (clientAccountId != null) return List.of();
        int boundedLimit = Math.max(1, Math.min(limit, 500));
        return auditLogs.findByTenantIdOrderByIdDesc(tenantId, PageRequest.of(0, boundedLimit)).stream()
            .map(AuditLogService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AuditLogResponse get(Long tenantId, Long clientAccountId, Long id) {
        if (clientAccountId != null) throw new ResourceNotFoundException("Audit log", id);
        return toResponse(auditLogs.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Audit log", id)));
    }

    private static AuditLogResponse toResponse(AuditLog row) {
        return new AuditLogResponse(row.getId(), row.getTenantId(), row.getWorkspaceId(), row.getActorUserId(),
            row.getActorMembershipId(), row.getAction(), row.getResourceType(), row.getResourceId(),
            row.getMetadataJson(), row.getCreatedAt());
    }

    private static HttpServletRequest currentRequest() {
        return RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes
            ? attributes.getRequest()
            : null;
    }

    public record AuditLogResponse(Long id, Long tenantId, Long workspaceId, Long actorUserId,
                                   Long actorMembershipId, String action, String resourceType, String resourceId,
                                   String metadataJson, OffsetDateTime createdAt) { }
}
