package com.nexa.platform.logistics.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "logistics_proof_of_delivery_records",
    indexes = @Index(name = "idx_logistics_pod_dispatch", columnList = "dispatch_order_id"))
public class ProofOfDeliveryRecord extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    @Column(nullable = false)
    private Long dispatchOrderId;
    @Column(length = 120)
    private String receivedBy;
    private OffsetDateTime completedAt;
    @Column(nullable = false)
    private boolean photoReference;
    @Column(nullable = false)
    private boolean signatureReference;
    @Column(length = 500)
    private String notes;
    @Column(nullable = false, length = 40)
    private String status;

    protected ProofOfDeliveryRecord() { }

    public ProofOfDeliveryRecord(Long tenantId, Long dispatchOrderId, String receivedBy, OffsetDateTime completedAt,
                                 Boolean photoReference, Boolean signatureReference, String notes, String status) {
        update(tenantId, dispatchOrderId, receivedBy, completedAt, photoReference, signatureReference, notes, status);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getDispatchOrderId() { return dispatchOrderId; }
    public String getReceivedBy() { return receivedBy; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public boolean isPhotoReference() { return photoReference; }
    public boolean isSignatureReference() { return signatureReference; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }

    public void update(Long tenantId, Long dispatchOrderId, String receivedBy, OffsetDateTime completedAt,
                       Boolean photoReference, Boolean signatureReference, String notes, String status) {
        this.tenantId = DispatchEvent.requireTenant(tenantId);
        if (dispatchOrderId == null) throw new IllegalArgumentException("Dispatch order id is required");
        this.dispatchOrderId = dispatchOrderId;
        this.receivedBy = receivedBy == null ? "" : receivedBy.trim();
        this.completedAt = completedAt;
        this.photoReference = photoReference != null && photoReference;
        this.signatureReference = signatureReference != null && signatureReference;
        this.notes = notes == null ? "" : notes.trim();
        this.status = status == null || status.isBlank() ? "pending" : status.trim().toLowerCase();
    }

    public void complete(String receivedBy, OffsetDateTime completedAt, Boolean photoReference, Boolean signatureReference, String notes) {
        this.receivedBy = DispatchEvent.require(receivedBy, "Receiver is required");
        this.completedAt = completedAt == null ? OffsetDateTime.now() : completedAt;
        this.photoReference = photoReference != null && photoReference;
        this.signatureReference = signatureReference != null && signatureReference;
        this.notes = notes == null ? "" : notes.trim();
        this.status = "completed";
    }
}
