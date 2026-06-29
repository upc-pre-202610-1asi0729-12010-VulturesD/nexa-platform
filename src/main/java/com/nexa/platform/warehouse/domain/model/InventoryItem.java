package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.catalog.domain.model.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_inventory_items", indexes = @Index(name = "idx_inventory_item_tenant", columnList = "tenant_id"))
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private Warehouse warehouse;
    @ManyToOne(optional = false)
    private Product product;
    @Column(nullable = false)
    private int quantityAvailable;
    @Column(nullable = false)
    private int quantityReserved;
    @Column(nullable = false)
    private int reorderPoint;
    protected InventoryItem() { }
    public InventoryItem(Warehouse warehouse, Product product, int quantityAvailable, int reorderPoint) {
        this(warehouse == null ? null : warehouse.getTenantId(), warehouse, product, quantityAvailable, reorderPoint);
    }
    public InventoryItem(Long tenantId, Warehouse warehouse, Product product, int quantityAvailable, int reorderPoint) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        if (warehouse == null || !tenantId.equals(warehouse.getTenantId())) throw new IllegalArgumentException("Warehouse must belong to the current tenant.");
        this.tenantId = tenantId; this.warehouse = warehouse; this.product = product; this.quantityAvailable = quantityAvailable;
        this.quantityReserved = 0; this.reorderPoint = reorderPoint;
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Warehouse getWarehouse() { return warehouse; }
    public Product getProduct() { return product; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public int getQuantityReserved() { return quantityReserved; }
    public int getReorderPoint() { return reorderPoint; }
    public boolean isLowStock() { return quantityAvailable <= reorderPoint; }
    public void apply(int quantityDelta) {
        if (quantityAvailable + quantityDelta < quantityReserved) {
            throw new IllegalStateException("Inventory movement would consume reserved stock.");
        }
        this.quantityAvailable += quantityDelta;
    }
    public void reserve(int units) {
        if (units <= 0) throw new IllegalArgumentException("Reservation units must be positive.");
        if (quantityReserved + units > quantityAvailable) {
            throw new IllegalStateException("Requested units exceed inventory availability.");
        }
        quantityReserved += units;
    }
    public void release(int units) {
        if (units <= 0 || units > quantityReserved) {
            throw new IllegalStateException("Released units exceed inventory reservation.");
        }
        quantityReserved -= units;
    }
}
