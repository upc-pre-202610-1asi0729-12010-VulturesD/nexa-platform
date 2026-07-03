package com.nexa.platform.invoicing.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoicing_invoice_lines")
public class InvoiceLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 140)
    private String description;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    protected InvoiceLine() { }
    public InvoiceLine(String description, int quantity, BigDecimal unitPrice) { this.description = description; this.quantity = quantity; this.unitPrice = unitPrice; }
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal subtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
