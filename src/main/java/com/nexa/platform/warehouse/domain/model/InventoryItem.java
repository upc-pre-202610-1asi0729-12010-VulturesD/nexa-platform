package com.nexa.platform.warehouse.domain.model;

import com.nexa.platform.catalog.domain.model.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_inventory_items")
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Warehouse warehouse;
    @ManyToOne(optional = false)
    private Product product;
    @Column(nullable = false)
    private int quantityAvailable;
    @Column(nullable = false)
    private int reorderPoint;
    protected InventoryItem() { }
    public InventoryItem(Warehouse warehouse, Product product, int quantityAvailable, int reorderPoint) {
        this.warehouse = warehouse; this.product = product; this.quantityAvailable = quantityAvailable; this.reorderPoint = reorderPoint;
    }
    public Long getId() { return id; }
    public Warehouse getWarehouse() { return warehouse; }
    public Product getProduct() { return product; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public int getReorderPoint() { return reorderPoint; }
    public boolean isLowStock() { return quantityAvailable <= reorderPoint; }
    public void apply(int quantityDelta) { this.quantityAvailable += quantityDelta; }
}
