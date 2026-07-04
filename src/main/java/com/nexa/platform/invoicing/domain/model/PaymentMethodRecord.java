package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "invoicing_payment_method_records",
    indexes = {
        @Index(name = "idx_invoicing_payment_methods_tenant", columnList = "tenant_id"),
        @Index(name = "idx_invoicing_payment_methods_client", columnList = "client_account_id")
    })
public class PaymentMethodRecord extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("active", "inactive", "disabled");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long clientAccountId;
    @Column(nullable = false, length = 60)
    private String type;
    @Column(nullable = false, length = 160)
    private String label;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false)
    private boolean isDefault;

    protected PaymentMethodRecord() { }

    public PaymentMethodRecord(Long tenantId, Long clientAccountId, String type, String label, boolean isDefault) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        if (clientAccountId == null || clientAccountId <= 0) throw new IllegalArgumentException("Client account is required.");
        this.tenantId = tenantId;
        this.clientAccountId = clientAccountId;
        this.type = require(type, "Payment method type is required.").toLowerCase();
        this.label = require(label, "Payment method label is required.");
        this.status = "active";
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getClientAccountId() { return clientAccountId; }
    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getStatus() { return status; }
    public boolean isDefault() { return isDefault; }

    public void changeStatus(String status, Boolean isDefault) {
        String normalized = require(status, "Payment method status is required.").toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Payment method status is not supported.");
        }
        this.status = normalized;
        if (isDefault != null) this.isDefault = isDefault;
        if (!"active".equals(normalized)) this.isDefault = false;
    }

    public void clearDefault() {
        this.isDefault = false;
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
