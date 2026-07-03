package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "invoicing_notification_records")
public class NotificationRecord extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long clientAccountId;
    @Column(nullable = false, length = 40)
    private String recipientRole;
    @Column(nullable = false, length = 40)
    private String type;
    @Column(nullable = false, length = 180)
    private String title;
    @Column(nullable = false, length = 1000)
    private String body;
    @Column(name = "is_read", nullable = false)
    private boolean read;

    protected NotificationRecord() { }

    public NotificationRecord(Long tenantId, Long clientAccountId, String recipientRole, String type,
                              String title, String body, boolean read) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        this.tenantId = tenantId;
        update(clientAccountId, recipientRole, type, title, body, read);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getClientAccountId() { return clientAccountId; }
    public String getRecipientRole() { return recipientRole; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public boolean isRead() { return read; }

    public void update(Long clientAccountId, String recipientRole, String type, String title, String body,
                       boolean read) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Notification title is required.");
        this.clientAccountId = clientAccountId;
        this.recipientRole = normalize(recipientRole, "buyer");
        this.type = normalize(type, "status");
        this.title = title.trim();
        this.body = body == null ? "" : body.trim();
        this.read = read;
    }

    public void markRead() { this.read = true; }

    private static String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim().toLowerCase();
    }
}
