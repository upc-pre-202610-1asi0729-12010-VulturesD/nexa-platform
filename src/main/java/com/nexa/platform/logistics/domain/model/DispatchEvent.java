package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "logistics_dispatch_events",
    indexes = @Index(name = "idx_logistics_dispatch_events_dispatch", columnList = "dispatch_order_id"))
public class DispatchEvent extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long dispatchOrderId;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false, length = 400)
    private String description;
    @Column(nullable = false)
    private boolean visibleToBuyer;

    protected DispatchEvent() { }

    public DispatchEvent(Long tenantId, Long dispatchOrderId, String status, String description, Boolean visibleToBuyer) {
        update(tenantId, dispatchOrderId, status, description, visibleToBuyer);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getDispatchOrderId() { return dispatchOrderId; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public boolean isVisibleToBuyer() { return visibleToBuyer; }

    public void update(Long tenantId, Long dispatchOrderId, String status, String description, Boolean visibleToBuyer) {
        this.tenantId = requireTenant(tenantId);
        if (dispatchOrderId == null) throw new IllegalArgumentException("Dispatch order id is required");
        this.dispatchOrderId = dispatchOrderId;
        this.status = require(status, "Dispatch event status is required");
        this.description = require(description, "Dispatch event description is required");
        this.visibleToBuyer = visibleToBuyer == null || visibleToBuyer;
    }

    static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required");
        return tenantId;
    }
}
