package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoicing_payments",
    indexes = {
        @Index(name = "idx_invoicing_payments_tenant", columnList = "tenant_id"),
        @Index(name = "idx_invoicing_payments_reference", columnList = "reference_code", unique = true)
    })
public class Payment extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private Invoice invoice;
    private Long clientAccountId;
    private Long paymentMethodRecordId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 8)
    private String currency;
    @Column(nullable = false, unique = true, length = 60)
    private String referenceCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;
    @Column(nullable = false, length = 40)
    private String method;
    @Column(length = 400)
    private String rejectionReason;
    private OffsetDateTime confirmedAt;
    private OffsetDateTime rejectedAt;
    protected Payment() { }
    public Payment(Invoice invoice, BigDecimal amount, String method) {
        this(1L, invoice, invoice.getOrder().getCustomer().getId(), null, amount, "PEN", method,
            "PAY-2026-" + String.format("%04d", invoice.getOrder().getId()));
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            this.status = PaymentStatus.CONFIRMED;
            this.confirmedAt = OffsetDateTime.now();
        }
    }
    public Payment(Long tenantId, Invoice invoice, Long clientAccountId, Long paymentMethodRecordId,
                   BigDecimal amount, String currency, String method, String referenceCode) {
        this.tenantId = requireId(tenantId, "Current tenant is required.");
        this.invoice = java.util.Objects.requireNonNull(invoice, "Invoice is required.");
        this.clientAccountId = clientAccountId;
        this.paymentMethodRecordId = paymentMethodRecordId;
        this.amount = requireAmount(amount);
        this.currency = normalizeCurrency(currency);
        this.method = requireText(method, "Payment method is required.");
        this.referenceCode = requireText(referenceCode, "Payment reference code is required.").toUpperCase();
        this.status = PaymentStatus.PENDING;
    }
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Invoice getInvoice() { return invoice; }
    public Long getClientAccountId() { return clientAccountId; }
    public Long getPaymentMethodRecordId() { return paymentMethodRecordId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getReferenceCode() { return referenceCode; }
    public PaymentStatus getStatus() { return status; }
    public String getMethod() { return method; }
    public String getRejectionReason() { return rejectionReason; }
    public OffsetDateTime getConfirmedAt() { return confirmedAt; }
    public OffsetDateTime getRejectedAt() { return rejectedAt; }

    public void update(Invoice invoice, Long clientAccountId, Long paymentMethodRecordId, BigDecimal amount,
                       String currency, String method, String referenceCode) {
        if (status != PaymentStatus.PENDING) throw new IllegalStateException("Only pending payments can be updated.");
        this.invoice = java.util.Objects.requireNonNull(invoice, "Invoice is required.");
        this.clientAccountId = clientAccountId;
        this.paymentMethodRecordId = paymentMethodRecordId;
        this.amount = requireAmount(amount);
        this.currency = normalizeCurrency(currency);
        this.method = requireText(method, "Payment method is required.");
        this.referenceCode = requireText(referenceCode, "Payment reference code is required.").toUpperCase();
    }

    public void confirm() {
        if (status == PaymentStatus.CONFIRMED) throw new IllegalStateException("Payment is already confirmed.");
        if (status != PaymentStatus.PENDING) throw new IllegalStateException("Only pending payments can be confirmed.");
        status = PaymentStatus.CONFIRMED;
        confirmedAt = OffsetDateTime.now();
        rejectedAt = null;
        rejectionReason = null;
    }

    public void reject(String reason) {
        if (status == PaymentStatus.CONFIRMED || status == PaymentStatus.PAID) {
            throw new IllegalStateException("Confirmed payments cannot be rejected.");
        }
        if (status == PaymentStatus.CANCELLED) throw new IllegalStateException("Cancelled payments cannot be rejected.");
        status = PaymentStatus.REJECTED;
        rejectionReason = reason == null ? "" : reason.trim();
        rejectedAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (status == PaymentStatus.CONFIRMED || status == PaymentStatus.PAID) {
            throw new IllegalStateException("Confirmed payments cannot be cancelled.");
        }
        status = PaymentStatus.CANCELLED;
    }

    private static Long requireId(Long value, String message) {
        if (value == null || value <= 0) throw new IllegalArgumentException(message);
        return value;
    }

    private static BigDecimal requireAmount(BigDecimal value) {
        if (value == null || value.signum() <= 0) throw new IllegalArgumentException("Payment amount must be positive.");
        return value;
    }

    private static String normalizeCurrency(String value) {
        String currency = value == null || value.isBlank() ? "PEN" : value.trim().toUpperCase();
        if (currency.length() != 3) throw new IllegalArgumentException("Payment currency must use an ISO 4217 code.");
        return currency;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
