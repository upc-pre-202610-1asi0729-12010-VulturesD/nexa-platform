package com.nexa.platform.tenantmanagement.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tenant_management_tenant_subscriptions",
    indexes = @Index(name = "idx_tenant_management_subscriptions_tenant", columnList = "tenant_id"))
public class TenantSubscription extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false, length = 60)
    private String plan;
    @Column(nullable = false)
    private int seats;
    @Column(nullable = false)
    private int warehouses;
    @Column(nullable = false, length = 40)
    private String paymentStatus;
    private LocalDate nextBillingDate;
    @Column(length = 180)
    private String billingContact;

    protected TenantSubscription() { }
    public TenantSubscription(Long tenantId, String plan, Integer seats, Integer warehouses, String paymentStatus,
                              LocalDate nextBillingDate, String billingContact) {
        update(tenantId, plan, seats, warehouses, paymentStatus, nextBillingDate, billingContact);
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getPlan() { return plan; }
    public int getSeats() { return seats; }
    public int getWarehouses() { return warehouses; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public String getBillingContact() { return billingContact; }
    public void update(Long tenantId, String plan, Integer seats, Integer warehouses, String paymentStatus,
                       LocalDate nextBillingDate, String billingContact) {
        if (tenantId == null) throw new IllegalArgumentException("Tenant id is required");
        this.tenantId = tenantId;
        this.plan = plan == null || plan.isBlank() ? "Business" : plan.trim();
        this.seats = seats == null ? 0 : Math.max(0, seats);
        this.warehouses = warehouses == null ? 0 : Math.max(0, warehouses);
        this.paymentStatus = paymentStatus == null || paymentStatus.isBlank() ? "review_active" : paymentStatus.trim();
        this.nextBillingDate = nextBillingDate;
        this.billingContact = TenantMember.optional(billingContact);
    }
}
