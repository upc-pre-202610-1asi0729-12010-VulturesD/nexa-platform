package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_tenants",
    indexes = {
        @Index(name = "idx_tenant_management_tenants_slug", columnList = "slug", unique = true),
        @Index(name = "idx_tenant_management_tenants_ruc", columnList = "ruc", unique = true)
    })
public class Tenant extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160)
    private String legalName;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, unique = true, length = 20)
    private String ruc;

    @Column(nullable = false, length = 240)
    private String workspaceUrl;

    @Column(length = 120)
    private String emailDomain;

    @Column(nullable = false, length = 40)
    private String plan;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(nullable = false, length = 80)
    private String country;

    protected Tenant() { }

    public Tenant(String name, String legalName, String slug, String ruc, String workspaceUrl, String emailDomain,
                  String plan, String status, String country) {
        update(name, legalName, slug, ruc, workspaceUrl, emailDomain, plan, status, country);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getSlug() { return slug; }
    public String getRuc() { return ruc; }
    public String getWorkspaceUrl() { return workspaceUrl; }
    public String getEmailDomain() { return emailDomain; }
    public String getPlan() { return plan; }
    public String getStatus() { return status; }
    public String getCountry() { return country; }

    public void update(String name, String legalName, String slug, String ruc, String workspaceUrl, String emailDomain,
                       String plan, String status, String country) {
        this.name = requireText(name, "Tenant name is required");
        this.legalName = requireText(legalName, "Tenant legal name is required");
        this.slug = requireText(slug, "Tenant slug is required").toLowerCase();
        this.ruc = requireText(ruc, "Tenant RUC is required");
        this.workspaceUrl = requireText(workspaceUrl, "Tenant workspace URL is required");
        this.emailDomain = normalizeOptional(emailDomain);
        this.plan = defaultText(plan, "Business");
        this.status = defaultText(status, "active");
        this.country = defaultText(country, "PE");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }
}
