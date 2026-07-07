package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_management_tenant_custom_fields",
    indexes = @Index(name = "idx_tenant_management_custom_fields_tenant", columnList = "tenant_id"))
public class TenantCustomField extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 80)
    private String code;
    @Column(nullable = false, length = 140)
    private String label;
    @Column(nullable = false, length = 80)
    private String targetResource;
    @Column(nullable = false, length = 40)
    private String fieldType;
    @Column(nullable = false)
    private boolean required;
    @Column(nullable = false)
    private boolean enabled;

    protected TenantCustomField() { }
    public TenantCustomField(Long tenantId, String code, String label, String targetResource, String fieldType,
                             Boolean required, Boolean enabled) {
        update(tenantId, code, label, targetResource, fieldType, required, enabled);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getTargetResource() { return targetResource; }
    public String getFieldType() { return fieldType; }
    public boolean isRequired() { return required; }
    public boolean isEnabled() { return enabled; }
    public void update(Long tenantId, String code, String label, String targetResource, String fieldType,
                       Boolean required, Boolean enabled) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.code = TenantMember.require(code, "Custom field code is required");
        this.label = TenantMember.require(label, "Custom field label is required");
        this.targetResource = targetResource == null || targetResource.isBlank() ? "Product" : targetResource.trim();
        this.fieldType = fieldType == null || fieldType.isBlank() ? "text" : fieldType.trim();
        this.required = required != null && required;
        this.enabled = enabled == null || enabled;
    }
}
