package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "invoicing_payment_process_records")
public class PaymentProcessRecord extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("pending", "confirmed", "failed");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long orderId;
    private Long clientAccountId;
    private Long paymentId;
    private Long paymentMethodRecordId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal shipping;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Column(nullable = false, length = 40)
    private String status;

    protected PaymentProcessRecord() { }

    public PaymentProcessRecord(Long tenantId, Long orderId, Long clientAccountId, Long paymentId,
                                Long paymentMethodRecordId, BigDecimal subtotal, BigDecimal discount,
                                BigDecimal shipping, BigDecimal igv, BigDecimal total, String status) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required.");
        this.tenantId = tenantId;
        this.orderId = orderId;
        this.clientAccountId = clientAccountId;
        this.paymentId = paymentId;
        this.paymentMethodRecordId = paymentMethodRecordId;
        this.subtotal = nonNegative(subtotal, "Subtotal");
        this.discount = nonNegative(discount, "Discount");
        this.shipping = nonNegative(shipping, "Shipping");
        this.igv = nonNegative(igv, "IGV");
        this.total = nonNegative(total, "Total");
        this.status = normalizeStatus(status == null || status.isBlank() ? "pending" : status);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getOrderId() { return orderId; }
    public Long getClientAccountId() { return clientAccountId; }
    public Long getPaymentId() { return paymentId; }
    public Long getPaymentMethodRecordId() { return paymentMethodRecordId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getShipping() { return shipping; }
    public BigDecimal getIgv() { return igv; }
    public BigDecimal getTotal() { return total; }
    public String getStatus() { return status; }

    public void changeStatus(String status) {
        String nextStatus = normalizeStatus(status);
        if ("confirmed".equals(this.status) && !"confirmed".equals(nextStatus)) {
            throw new IllegalStateException("Confirmed payment processes cannot move backwards.");
        }
        if ("failed".equals(this.status) && "confirmed".equals(nextStatus)) {
            throw new IllegalStateException("Failed payment processes must be reviewed before confirmation.");
        }
        this.status = nextStatus;
    }

    private static BigDecimal nonNegative(BigDecimal value, String label) {
        BigDecimal safe = value == null ? BigDecimal.ZERO : value;
        if (safe.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException(label + " cannot be negative.");
        return safe;
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Payment process status is required.");
        String normalized = status.trim().toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) throw new IllegalArgumentException("Payment process status is not supported.");
        return normalized;
    }
}
