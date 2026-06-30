package com.nexa.platform.sales.domain.model;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_purchase_request_lines")
public class PurchaseRequestLine extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(name = "purchase_request_id", insertable = false, updatable = false)
    private Long purchaseRequestId;
    @ManyToOne(optional = false)
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, length = 32)
    private String unit;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedWeightKg;
    @Column(length = 240)
    private String notes;

    protected PurchaseRequestLine() { }

    public PurchaseRequestLine(Long tenantId, Product product, int quantity, String unit, BigDecimal estimatedWeightKg, String notes) {
        if (product == null) throw new IllegalArgumentException("Product is required");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.product = product;
        this.quantity = quantity;
        this.unit = unit == null || unit.isBlank() ? product.getUnit() : unit.trim();
        this.estimatedWeightKg = estimatedWeightKg == null ? BigDecimal.ZERO : estimatedWeightKg;
        this.notes = notes == null ? "" : notes.trim();
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getPurchaseRequestId() { return purchaseRequestId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public BigDecimal getEstimatedWeightKg() { return estimatedWeightKg; }
    public String getNotes() { return notes; }

    public void update(Long tenantId, Product product, int quantity, String unit, BigDecimal estimatedWeightKg,
                       String notes) {
        if (product == null) throw new IllegalArgumentException("Product is required");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        this.tenantId = tenantId == null ? this.tenantId : tenantId;
        this.product = product;
        this.quantity = quantity;
        this.unit = unit == null || unit.isBlank() ? product.getUnit() : unit.trim();
        this.estimatedWeightKg = estimatedWeightKg == null ? BigDecimal.ZERO : estimatedWeightKg;
        this.notes = notes == null ? "" : notes.trim();
    }
}
