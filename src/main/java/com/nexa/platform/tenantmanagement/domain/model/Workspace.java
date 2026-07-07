package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_workspaces",
    indexes = {
        @Index(name = "idx_tenant_management_workspaces_tenant", columnList = "tenant_id"),
        @Index(name = "idx_tenant_management_workspaces_slug", columnList = "slug", unique = true)
    })
public class Workspace extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, length = 240)
    private String url;

    @Column(length = 120)
    private String emailDomain;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false)
    private boolean primaryWorkspace;

    protected Workspace() { }

    public Workspace(Long tenantId, String name, String slug, String url, String emailDomain, String status, Boolean primaryWorkspace) {
        update(tenantId, name, slug, url, emailDomain, status, primaryWorkspace);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getUrl() { return url; }
    public String getEmailDomain() { return emailDomain; }
    public String getStatus() { return status; }
    public boolean isPrimaryWorkspace() { return primaryWorkspace; }

    public void update(Long tenantId, String name, String slug, String url, String emailDomain, String status, Boolean primaryWorkspace) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.name = requireText(name, "Workspace name is required");
        this.slug = requireText(slug, "Workspace slug is required").toLowerCase();
        this.url = requireText(url, "Workspace URL is required");
        this.emailDomain = emailDomain == null || emailDomain.isBlank() ? null : emailDomain.trim().toLowerCase();
        this.status = status == null || status.isBlank() ? "active" : status.trim();
        this.primaryWorkspace = primaryWorkspace != null && primaryWorkspace;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
