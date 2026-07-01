package com.nexa.platform.sales.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sales_credit_requests",
    uniqueConstraints = @UniqueConstraint(name = "uk_sales_credit_request_tenant_code",
        columnNames = {"tenant_id", "code"}))
public class CreditRequest extends AuditableEntity {
    private static final Set<String> RESOLUTIONS = Set.of("approved", "rejected", "cancelled");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    @ManyToOne(optional = false)
    private Customer clientAccount;
    @Column(nullable = false, length = 40)
    private String code;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal requestedAmount;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(nullable = false, length = 40)
    private String status = "submitted";
    private Long createdByUserId;
    @Column(nullable = false, length = 120)
    private String reviewedBy = "";
    @Column(nullable = false, length = 500)
    private String resolutionNote = "";

    protected CreditRequest() { }

    public CreditRequest(Long tenantId, Customer clientAccount, String code, BigDecimal requestedAmount,
                         String reason, Long createdByUserId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id must be positive.");
        if (clientAccount == null || !tenantId.equals(clientAccount.getTenantId())) {
            throw new IllegalArgumentException("Client account does not belong to the current tenant.");
        }
        if (requestedAmount == null || requestedAmount.signum() <= 0) {
            throw new IllegalArgumentException("Requested credit amount must be positive.");
        }
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Credit request reason is required.");
        this.tenantId = tenantId;
        this.clientAccount = clientAccount;
        this.code = normalizeCode(code);
        this.requestedAmount = requestedAmount;
        this.reason = reason.trim();
        this.createdByUserId = createdByUserId;
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Customer getClientAccount() { return clientAccount; }
    public String getCode() { return code; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public String getReviewedBy() { return reviewedBy; }
    public String getResolutionNote() { return resolutionNote; }

    public void resolve(String status, String reviewedBy, String note) {
        if (!"submitted".equals(this.status)) {
            throw new IllegalStateException("Only submitted credit requests can be resolved.");
        }
        String next = status == null ? "" : status.trim().toLowerCase();
        if (!RESOLUTIONS.contains(next)) throw new IllegalArgumentException("Unsupported credit request resolution.");
        this.status = next;
        this.reviewedBy = reviewedBy == null ? "" : reviewedBy.trim();
        this.resolutionNote = note == null ? "" : note.trim();
    }

    private static String normalizeCode(String code) {
        if (code != null && !code.isBlank()) return code.trim().toUpperCase();
        return "CRQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
