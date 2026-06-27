package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_inventory_reservations",
    indexes = {
        @Index(name = "idx_warehouse_reservations_tenant", columnList = "tenant_id"),
        @Index(name = "idx_warehouse_reservations_item", columnList = "inventory_item_id")
    })
public class InventoryReservationRecord extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private InventoryItem inventoryItem;
    @ManyToOne
    private StockBatch inventoryLot;
    private Long orderId;
    private Long purchaseRequestId;
    @Column(nullable = false, length = 60)
    private String code;
    @Column(nullable = false)
    private int units;
    @Column(nullable = false, length = 40)
    private String status;

    protected InventoryReservationRecord() { }

    public InventoryReservationRecord(Long tenantId, InventoryItem inventoryItem, StockBatch inventoryLot,
                                      Long orderId, Long purchaseRequestId, String code, int units) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        if (inventoryItem == null) throw new IllegalArgumentException("Inventory item is required.");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Reservation code is required.");
        if (units <= 0) throw new IllegalArgumentException("Reservation units must be positive.");
        this.tenantId = tenantId;
        this.inventoryItem = inventoryItem;
        this.inventoryLot = inventoryLot;
        this.orderId = orderId;
        this.purchaseRequestId = purchaseRequestId;
        this.code = code.trim().toUpperCase();
        this.units = units;
        this.status = "reserved";
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public StockBatch getInventoryLot() { return inventoryLot; }
    public Long getOrderId() { return orderId; }
    public Long getPurchaseRequestId() { return purchaseRequestId; }
    public String getCode() { return code; }
    public int getUnits() { return units; }
    public String getStatus() { return status; }

    public boolean release() {
        if ("released".equals(status)) return false;
        status = "released";
        return true;
    }
}
