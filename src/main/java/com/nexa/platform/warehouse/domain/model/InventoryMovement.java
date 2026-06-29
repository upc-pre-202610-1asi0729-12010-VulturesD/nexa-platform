package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "warehouse_inventory_movements",
    uniqueConstraints = @UniqueConstraint(name = "uk_inventory_movement_tenant_code", columnNames = {"tenant_id", "code"}),
    indexes = @Index(name = "idx_inventory_movement_tenant", columnList = "tenant_id"))
public class InventoryMovement extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private InventoryItem item;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MovementType type;
    @Column(nullable = false)
    private int quantityDelta;
    @Column(nullable = false, length = 60)
    private String code;
    @Column(length = 60)
    private String lotCode;
    @Column(length = 120)
    private String warehouse;
    @Column(length = 60)
    private String orderReference;
    @Column(nullable = false, length = 240)
    private String reason;
    @Column(length = 240)
    private String note;
    @Column(precision = 6, scale = 2)
    private BigDecimal temperatureReading;
    @Column(nullable = false, length = 120)
    private String performedBy;
    @Column(nullable = false)
    private OffsetDateTime occurredAt;

    protected InventoryMovement() { }

    public InventoryMovement(InventoryItem item, MovementType type, int quantityDelta, String note) {
        this(1L, item, "MOV-" + System.currentTimeMillis(), type, quantityDelta, null,
            item == null ? null : item.getWarehouse().getName(), null, type.name().toLowerCase(), note, null, "",
            OffsetDateTime.now());
    }

    public InventoryMovement(Long tenantId, InventoryItem item, String code, MovementType type, int quantityDelta,
                             String lotCode, String warehouse, String orderReference, String reason, String note,
                             BigDecimal temperatureReading, String performedBy, OffsetDateTime occurredAt) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        if (item == null) throw new IllegalArgumentException("Inventory item is required.");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Movement code is required.");
        if (type == null) throw new IllegalArgumentException("Movement type is required.");
        if (quantityDelta == 0) throw new IllegalArgumentException("Movement quantity cannot be zero.");
        this.tenantId = tenantId;
        this.item = item;
        this.code = code.trim().toUpperCase();
        this.type = type;
        this.quantityDelta = quantityDelta;
        this.lotCode = optional(lotCode);
        this.warehouse = optional(warehouse);
        this.orderReference = optional(orderReference);
        this.reason = reason == null || reason.isBlank() ? type.name().toLowerCase() : reason.trim();
        this.note = optional(note);
        this.temperatureReading = temperatureReading;
        this.performedBy = optional(performedBy);
        this.occurredAt = occurredAt == null ? OffsetDateTime.now() : occurredAt;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public InventoryItem getItem() { return item; }
    public MovementType getType() { return type; }
    public int getQuantityDelta() { return quantityDelta; }
    public String getCode() { return code; }
    public String getLotCode() { return lotCode; }
    public String getWarehouse() { return warehouse; }
    public String getOrderReference() { return orderReference; }
    public String getReason() { return reason; }
    public String getNote() { return note; }
    public BigDecimal getTemperatureReading() { return temperatureReading; }
    public String getPerformedBy() { return performedBy; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }

    private static String optional(String value) { return value == null ? "" : value.trim(); }
}
