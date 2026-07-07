package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_workspace_features",
    indexes = @Index(name = "idx_tenant_management_features_workspace", columnList = "workspace_id"))
public class WorkspaceFeature extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long workspaceId;
    @Column(nullable = false, length = 80)
    private String code;
    @Column(nullable = false, length = 140)
    private String name;
    @Column(nullable = false)
    private boolean enabled;

    protected WorkspaceFeature() { }
    public WorkspaceFeature(Long workspaceId, String code, String name, Boolean enabled) {
        update(workspaceId, code, name, enabled);
    }
    public Long getId() { return id; }
    public Long getWorkspaceId() { return workspaceId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void update(Long workspaceId, String code, String name, Boolean enabled) {
        if (workspaceId == null) throw new IllegalArgumentException("Workspace id is required");
        this.workspaceId = workspaceId;
        this.code = TenantMember.require(code, "Feature code is required");
        this.name = TenantMember.require(name, "Feature name is required");
        this.enabled = enabled == null || enabled;
    }
}
