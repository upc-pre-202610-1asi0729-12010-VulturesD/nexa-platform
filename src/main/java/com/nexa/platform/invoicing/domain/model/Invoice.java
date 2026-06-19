package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoicing_invoices")
public class Invoice extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private SalesOrder order;
    @Column(nullable = false, length = 40)
    private String invoiceNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceStatus status = InvoiceStatus.ISSUED;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceLine> lines = new ArrayList<>();
    protected Invoice() { }
    public Invoice(SalesOrder order, String invoiceNumber) { this.order = order; this.invoiceNumber = invoiceNumber; }
    public Long getId() { return id; }
    public SalesOrder getOrder() { return order; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public InvoiceStatus getStatus() { return status; }
    public List<InvoiceLine> getLines() { return lines; }
    public void addLine(InvoiceLine line) { this.lines.add(line); }
    public BigDecimal total() { return lines.stream().map(InvoiceLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public void markPaid() { this.status = InvoiceStatus.PAID; }
}
