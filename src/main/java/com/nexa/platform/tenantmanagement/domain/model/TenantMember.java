package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_tenant_members",
    indexes = @Index(name = "idx_tenant_management_members_tenant", columnList = "tenant_id"))
public class TenantMember extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
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

    protected TenantMember() { }
    public TenantMember(Long tenantId, String email, String fullName, String role, String department, String status) {
        update(tenantId, email, fullName, role, department, status);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
    public void update(Long tenantId, String email, String fullName, String role, String department, String status) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.email = require(email, "Member email is required").toLowerCase();
        this.fullName = require(fullName, "Member full name is required");
        this.role = require(role, "Member role is required");
        this.department = optional(department);
        this.status = status == null || status.isBlank() ? "active" : status.trim();
    }
    static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
    static String optional(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
