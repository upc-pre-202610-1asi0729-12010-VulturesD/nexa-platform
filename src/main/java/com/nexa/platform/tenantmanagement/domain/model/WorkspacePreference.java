package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_workspace_preferences",
    indexes = @Index(name = "idx_tenant_management_preferences_workspace", columnList = "workspace_id"))
public class WorkspacePreference extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long workspaceId;
    @Column(name = "preference_key", nullable = false, length = 80)
    private String key;
    @Column(name = "preference_value", nullable = false, length = 500)
    private String value;
    @Column(nullable = false, length = 40)
    private String valueType;

    protected WorkspacePreference() { }
    public WorkspacePreference(Long tenantId, Long workspaceId, String key, String value, String valueType) {
        update(tenantId, workspaceId, key, value, valueType);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getWorkspaceId() { return workspaceId; }
    public String getKey() { return key; }
    public String getValue() { return value; }
    public String getValueType() { return valueType; }
    public void update(Long tenantId, Long workspaceId, String key, String value, String valueType) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        if (workspaceId == null) throw new IllegalArgumentException("Workspace id is required");
        this.tenantId = tenantId;
        this.workspaceId = workspaceId;
        this.key = TenantMember.require(key, "Preference key is required");
        this.value = TenantMember.require(value, "Preference value is required");
        this.valueType = valueType == null || valueType.isBlank() ? "string" : valueType.trim();
    }
}
