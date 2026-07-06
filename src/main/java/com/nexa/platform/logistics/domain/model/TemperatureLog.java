package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "logistics_temperature_logs",
    indexes = @Index(name = "idx_logistics_temperature_dispatch", columnList = "dispatch_order_id"))
public class TemperatureLog extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long dispatchOrderId;
    private Long orderId;
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal celsius;
    @Column(nullable = false, length = 80)
    private String zone;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false)
    private OffsetDateTime recordedAt;

    protected TemperatureLog() { }

    public TemperatureLog(Long tenantId, Long dispatchOrderId, Long orderId, BigDecimal celsius, String zone,
                          String status, OffsetDateTime recordedAt) {
        update(tenantId, dispatchOrderId, orderId, celsius, zone, status, recordedAt);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getDispatchOrderId() { return dispatchOrderId; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getCelsius() { return celsius; }
    public String getZone() { return zone; }
    public String getStatus() { return status; }
    public OffsetDateTime getRecordedAt() { return recordedAt; }

    public void update(Long tenantId, Long dispatchOrderId, Long orderId, BigDecimal celsius, String zone,
                       String status, OffsetDateTime recordedAt) {
        this.tenantId = DispatchEvent.requireTenant(tenantId);
        this.dispatchOrderId = dispatchOrderId;
        this.orderId = orderId;
        if (celsius == null) throw new IllegalArgumentException("Temperature value is required");
        this.celsius = celsius;
        this.zone = DispatchEvent.require(zone, "Temperature zone is required");
        this.status = status == null || status.isBlank() ? "ok" : status.trim().toLowerCase();
        this.recordedAt = recordedAt == null ? OffsetDateTime.now() : recordedAt;
    }

    public void resolveAlert() {
        this.status = "resolved";
    }
}
