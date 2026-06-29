package com.nexa.platform.shared.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shared_audit_logs")
public class AuditLog extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long workspaceId;
    @Column(nullable = false)
    private Long actorUserId;
    private Long actorMembershipId;
    @Column(nullable = false, length = 120)
    private String action;
    @Column(nullable = false, length = 80)
    private String resourceType;
    @Column(nullable = false, length = 120)
    private String resourceId;
    @Column(length = 2000)
    private String metadataJson;
    @Column(length = 80)
    private String ipAddress;
    @Column(length = 500)
    private String userAgent;

    protected AuditLog() { }

    public AuditLog(Long tenantId, Long workspaceId, Long actorUserId, Long actorMembershipId,
                    String action, String resourceType, String resourceId, String metadataJson,
                    String ipAddress, String userAgent) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        this.tenantId = tenantId;
        this.workspaceId = workspaceId;
        this.actorUserId = actorUserId == null ? 0L : actorUserId;
        this.actorMembershipId = actorMembershipId;
        this.action = require(action, "Audit action is required.");
        this.resourceType = require(resourceType, "Audit resource type is required.");
        this.resourceId = require(resourceId, "Audit resource id is required.");
        this.metadataJson = metadataJson;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getWorkspaceId() { return workspaceId; }
    public Long getActorUserId() { return actorUserId; }
    public Long getActorMembershipId() { return actorMembershipId; }
    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getMetadataJson() { return metadataJson; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
