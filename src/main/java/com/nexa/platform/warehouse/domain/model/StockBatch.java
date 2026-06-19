package com.nexa.platform.warehouse.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "warehouse_stock_batches")
public class StockBatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private InventoryItem item;
    @Column(nullable = false, length = 60)
    private String lotCode;
    @Column(nullable = false)
    private LocalDate expirationDate;
    @Column(nullable = false)
    private int quantity;
    protected StockBatch() { }
    public StockBatch(InventoryItem item, String lotCode, LocalDate expirationDate, int quantity) { this.item = item; this.lotCode = lotCode; this.expirationDate = expirationDate; this.quantity = quantity; }
    public Long getId() { return id; }
    public InventoryItem getItem() { return item; }
    public String getLotCode() { return lotCode; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getQuantity() { return quantity; }
}
