package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.NotificationRecordDtos;
import com.nexa.platform.invoicing.domain.model.NotificationRecord;
import com.nexa.platform.invoicing.domain.model.repositories.NotificationRecordRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.shared.application.auditing.AuditLogService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationRecordService {
    private final NotificationRecordRepositoryPort notifications;
    private final CustomerRepositoryPort customers;
    private final AuditLogService auditLog;

    public NotificationRecordService(NotificationRecordRepositoryPort notifications,
                                     CustomerRepositoryPort customers, AuditLogService auditLog) {
        this.notifications = notifications;
        this.customers = customers;
        this.auditLog = auditLog;
    }

    @Transactional(readOnly = true)
    public List<NotificationRecordDtos.NotificationRecordResponse> list(Long tenantId, Long clientAccountId) {
        requireTenant(tenantId);
        List<NotificationRecord> rows = clientAccountId == null
            ? notifications.findByTenantIdOrderByReadAscIdDesc(tenantId)
            : notifications.findByTenantIdAndClientAccountIdOrderByReadAscIdDesc(tenantId, clientAccountId);
        return rows.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NotificationRecordDtos.NotificationRecordResponse get(Long tenantId, Long clientAccountId, Long id) {
        return toResponse(find(tenantId, clientAccountId, id));
    }

    @Transactional
    public NotificationRecordDtos.NotificationRecordResponse create(
        Long tenantId, NotificationRecordDtos.UpsertNotificationRecordRequest request) {
        requireTenant(tenantId);
        validateClient(tenantId, request.clientAccountId());
        NotificationRecord saved = notifications.save(new NotificationRecord(tenantId, request.clientAccountId(),
            request.recipientRole(), request.type(), request.title(), request.body(), request.read()));
        auditLog.record(tenantId, "notification.created", "notification", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public NotificationRecordDtos.NotificationRecordResponse update(
        Long tenantId, Long id, NotificationRecordDtos.UpsertNotificationRecordRequest request) {
        NotificationRecord notification = find(tenantId, null, id);
        validateClient(tenantId, request.clientAccountId());
        notification.update(request.clientAccountId(), request.recipientRole(), request.type(),
            request.title(), request.body(), request.read());
        NotificationRecord saved = notifications.save(notification);
        auditLog.record(tenantId, "notification.updated", "notification", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public NotificationRecordDtos.NotificationRecordResponse markRead(Long tenantId, Long clientAccountId, Long id) {
        NotificationRecord notification = find(tenantId, clientAccountId, id);
        notification.markRead();
        NotificationRecord saved = notifications.save(notification);
        auditLog.record(tenantId, "notification.read", "notification", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long tenantId, Long id) {
        NotificationRecord notification = find(tenantId, null, id);
        notifications.delete(notification);
        auditLog.record(tenantId, "notification.deleted", "notification", id, null);
    }

    private NotificationRecord find(Long tenantId, Long clientAccountId, Long id) {
        requireTenant(tenantId);
        return (clientAccountId == null
            ? notifications.findByIdAndTenantId(id, tenantId)
            : notifications.findByIdAndTenantIdAndClientAccountId(id, tenantId, clientAccountId))
            .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
    }

    private void validateClient(Long tenantId, Long clientAccountId) {
        if (clientAccountId != null && customers.findByIdAndTenantId(clientAccountId, tenantId).isEmpty()) {
            throw new IllegalArgumentException("Client account does not belong to the current tenant.");
        }
    }

    private NotificationRecordDtos.NotificationRecordResponse toResponse(NotificationRecord notification) {
        return new NotificationRecordDtos.NotificationRecordResponse(notification.getId(),
            notification.getTenantId(), notification.getClientAccountId(), notification.getRecipientRole(),
            notification.getType(), notification.getTitle(), notification.getBody(), notification.isRead(),
            notification.getCreatedAt(), notification.getUpdatedAt());
    }

    private static void requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
    }
}
