package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_user_workspace_memberships",
    indexes = {
        @Index(name = "idx_tenant_management_user_memberships_tenant", columnList = "tenant_id"),
        @Index(name = "idx_tenant_management_user_memberships_workspace", columnList = "workspace_id")
    })
public class UserWorkspaceMembership extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long workspaceId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, length = 180)
    private String email;
    @Column(nullable = false, length = 120)
    private String fullName;
    @Column(nullable = false, length = 60)
    private String role;
    @Column(length = 80)
    private String department;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false, length = 60)
    private String portalAccess;
    private Long clientAccountId;

    protected UserWorkspaceMembership() { }
    public UserWorkspaceMembership(Long tenantId, Long workspaceId, Long userId, String email, String fullName, String role,
                                   String department, String status, String portalAccess, Long clientAccountId) {
        update(tenantId, workspaceId, userId, email, fullName, role, department, status, portalAccess, clientAccountId);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getWorkspaceId() { return workspaceId; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public String getPortalAccess() { return portalAccess; }
    public Long getClientAccountId() { return clientAccountId; }
    public void update(Long tenantId, Long workspaceId, Long userId, String email, String fullName, String role,
                       String department, String status, String portalAccess, Long clientAccountId) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        if (workspaceId == null) throw new IllegalArgumentException("Workspace id is required");
        if (userId == null) throw new IllegalArgumentException("User id is required");
        this.tenantId = tenantId;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.email = TenantMember.require(email, "Membership email is required").toLowerCase();
        this.fullName = TenantMember.require(fullName, "Membership full name is required");
        this.role = TenantMember.require(role, "Membership role is required");
        this.department = TenantMember.optional(department);
        this.status = status == null || status.isBlank() ? "active" : status.trim();
        this.portalAccess = portalAccess == null || portalAccess.isBlank() ? "internal" : portalAccess.trim();
        this.clientAccountId = clientAccountId;
    }
}
