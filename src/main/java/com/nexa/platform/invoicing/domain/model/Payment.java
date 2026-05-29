package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoicing_payments")
public class Payment extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Invoice invoice;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 40)
    private String method;
    protected Payment() { }
    public Payment(Invoice invoice, BigDecimal amount, String method) { this.invoice = invoice; this.amount = amount; this.method = method; }
    public Long getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public BigDecimal getAmount() { return amount; }
    public String getMethod() { return method; }
}
