package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoicing_invoices")
public class Invoice extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private SalesOrder order;
    @Column(nullable = false, length = 40)
    private String invoiceNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceStatus status = InvoiceStatus.ISSUED;
    @Column(nullable = false, length = 3)
    private String currency = "PEN";
    private OffsetDateTime paidAt;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceLine> lines = new ArrayList<>();
    protected Invoice() { }
    public Invoice(Long tenantId, SalesOrder order, String invoiceNumber, String currency) {
        this.tenantId = requireTenant(tenantId);
        this.order = requireOrder(order);
        this.invoiceNumber = requireInvoiceNumber(invoiceNumber);
        this.currency = normalizeCurrency(currency);
    }
    public Invoice(SalesOrder order, String invoiceNumber) { this(1L, order, invoiceNumber, "PEN"); }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public SalesOrder getOrder() { return order; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public InvoiceStatus getStatus() { return status; }
    public String getCurrency() { return currency; }
    public OffsetDateTime getPaidAt() { return paidAt; }
    public List<InvoiceLine> getLines() { return lines; }
    public void addLine(InvoiceLine line) { this.lines.add(line); }
    public BigDecimal total() { return lines.stream().map(InvoiceLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public void update(SalesOrder order, String invoiceNumber, String currency, List<InvoiceLine> lines) {
        if (status == InvoiceStatus.PAID) throw new IllegalStateException("Paid invoices cannot be updated.");
        if (status == InvoiceStatus.VOIDED) throw new IllegalStateException("Voided invoices cannot be updated.");
        this.order = requireOrder(order);
        this.invoiceNumber = requireInvoiceNumber(invoiceNumber);
        this.currency = normalizeCurrency(currency);
        this.lines.clear();
        this.lines.addAll(lines);
    }
    public void markPaid() {
        if (status == InvoiceStatus.VOIDED) throw new IllegalStateException("Voided invoices cannot be paid.");
        if (status == InvoiceStatus.PAID) throw new IllegalStateException("Invoice is already paid.");
        this.status = InvoiceStatus.PAID;
        this.paidAt = OffsetDateTime.now();
    }
    public void voidInvoice() {
        if (status == InvoiceStatus.PAID) throw new IllegalStateException("Paid invoices cannot be voided.");
        this.status = InvoiceStatus.VOIDED;
        this.paidAt = null;
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        return tenantId;
    }

    private static SalesOrder requireOrder(SalesOrder order) {
        if (order == null) throw new IllegalArgumentException("Sales order is required.");
        return order;
    }

    private static String requireInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isBlank()) throw new IllegalArgumentException("Invoice number is required.");
        return invoiceNumber.trim().toUpperCase();
    }

    private static String normalizeCurrency(String currency) {
        String normalized = currency == null || currency.isBlank() ? "PEN" : currency.trim().toUpperCase();
        if (normalized.length() != 3) throw new IllegalArgumentException("Currency must use a 3-letter code.");
        return normalized;
    }
}
