package com.nexa.platform.sales.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_orders")
public class SalesOrder extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Customer customer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.CONFIRMED;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<SalesOrderItem> items = new ArrayList<>();
    protected SalesOrder() { }
    public SalesOrder(Customer customer) { this.customer = customer; }
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public OrderStatus getStatus() { return status; }
    public List<SalesOrderItem> getItems() { return items; }
    public void addItem(SalesOrderItem item) { this.items.add(item); }
    public void changeStatus(OrderStatus status) { this.status = status; }
    public BigDecimal total() { return items.stream().map(SalesOrderItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
}
