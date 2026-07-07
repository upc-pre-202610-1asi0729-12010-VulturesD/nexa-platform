package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_tenant_rules",
    indexes = @Index(name = "idx_tenant_management_rules_tenant", columnList = "tenant_id"))
public class TenantRule extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 80)
    private String code;
    @Column(nullable = false, length = 160)
    private String name;
    @Column(length = 360)
    private String description;
    @Column(nullable = false, length = 60)
    private String category;
    @Column(nullable = false)
    private boolean enabled;

    protected TenantRule() { }
    public TenantRule(Long tenantId, String code, String name, String description, String category, Boolean enabled) {
        update(tenantId, code, name, description, category, enabled);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public void update(Long tenantId, String code, String name, String description, String category, Boolean enabled) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.code = TenantMember.require(code, "Rule code is required");
        this.name = TenantMember.require(name, "Rule name is required");
        this.description = TenantMember.optional(description);
        this.category = category == null || category.isBlank() ? "operations" : category.trim();
        this.enabled = enabled == null || enabled;
    }
}
