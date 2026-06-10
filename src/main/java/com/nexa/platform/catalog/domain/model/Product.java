package com.nexa.platform.catalog.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_products", indexes = @Index(name = "idx_catalog_products_sku", columnList = "sku", unique = true))
public class Product extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String sku;
    @Column(nullable = false, length = 140)
    private String name;
    @Column(length = 360)
    private String description;
    @ManyToOne(optional = false)
    private Category category;
    @Column(nullable = false, length = 120)
    private String supplierName;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @Embedded
    private ColdChainRequirement coldChainRequirement;
    @Column(nullable = false)
    private boolean active = true;
    protected Product() { }
    public Product(String sku, String name, String description, Category category, String supplierName, BigDecimal unitPrice, ColdChainRequirement coldChainRequirement) {
        this.sku = sku; this.name = name; this.description = description; this.category = category; this.supplierName = supplierName; this.unitPrice = unitPrice; this.coldChainRequirement = coldChainRequirement;
    }
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public String getSupplierName() { return supplierName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public ColdChainRequirement getColdChainRequirement() { return coldChainRequirement; }
    public boolean isActive() { return active; }
    public void update(String name, String description, Category category, String supplierName, BigDecimal unitPrice, ColdChainRequirement requirement) {
        this.name = name; this.description = description; this.category = category; this.supplierName = supplierName; this.unitPrice = unitPrice; this.coldChainRequirement = requirement;
    }
    public void deactivate() { this.active = false; }
}
