package com.nexa.platform.sales.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
public class SalesOrder extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private Customer customer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;
    @Column(nullable = false, length = 20)
    private String priority = "normal";
    @Column(nullable = false, length = 800)
    private String notes = "";
    @Column(length = 120)
    private String paymentConfirmation;
    @Column(length = 120)
    private String inventoryReservation;
    @Column(length = 400)
    private String rejectionReason;
    private OffsetDateTime confirmedAt;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<SalesOrderItem> items = new ArrayList<>();
    protected SalesOrder() { }
    public SalesOrder(Long tenantId, Customer customer) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        this.tenantId = tenantId;
        this.customer = customer;
    }
    public SalesOrder(Customer customer) { this(1L, customer); }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Customer getCustomer() { return customer; }
    public OrderStatus getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getNotes() { return notes; }
    public String getPaymentConfirmation() { return paymentConfirmation; }
    public String getInventoryReservation() { return inventoryReservation; }
    public String getRejectionReason() { return rejectionReason; }
    public OffsetDateTime getConfirmedAt() { return confirmedAt; }
    public List<SalesOrderItem> getItems() { return items; }
    public void addItem(SalesOrderItem item) { this.items.add(item); }
    public void changeStatus(OrderStatus status) { this.status = status; }

    public void update(Customer customer, List<SalesOrderItem> items, String priority, String notes) {
        if (status != OrderStatus.PENDING) throw new IllegalStateException("Only pending orders can be updated.");
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("An order must contain at least one item.");
        this.customer = customer;
        this.items.clear();
        this.items.addAll(items);
        this.priority = normalizePriority(priority);
        this.notes = notes == null ? "" : notes.trim();
    }

    public void confirm(String paymentConfirmation, String inventoryReservation) {
        if (status == OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled orders cannot be confirmed.");
        if (status == OrderStatus.REJECTED) throw new IllegalStateException("Rejected orders cannot be confirmed.");
        this.paymentConfirmation = require(paymentConfirmation, "Payment confirmation is required.");
        this.inventoryReservation = require(inventoryReservation, "Inventory reservation is required.");
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = OffsetDateTime.now();
    }

    public void reject(String rejectionReason) {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.PAID) {
            throw new IllegalStateException("Confirmed or paid orders cannot be rejected.");
        }
        this.rejectionReason = require(rejectionReason, "Rejection reason is required.");
        this.status = OrderStatus.REJECTED;
    }

    public void cancel() {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.PAID) {
            throw new IllegalStateException("Confirmed or paid orders cannot be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public BigDecimal total() { return items.stream().map(SalesOrderItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }

    private static String normalizePriority(String value) {
        String normalized = value == null || value.isBlank() ? "normal" : value.trim().toLowerCase();
        if (!List.of("low", "normal", "medium", "high", "urgent").contains(normalized)) {
            throw new IllegalArgumentException("Unsupported order priority '" + normalized + "'.");
        }
        return normalized;
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
