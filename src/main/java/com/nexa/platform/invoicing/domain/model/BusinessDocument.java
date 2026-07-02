package com.nexa.platform.invoicing.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import com.nexa.platform.shared.domain.model.DocumentType;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "invoicing_business_documents",
    indexes = {
        @Index(name = "idx_invoicing_business_documents_order", columnList = "order_id"),
        @Index(name = "idx_invoicing_business_documents_tenant", columnList = "tenant_id")
    })
public class BusinessDocument extends AuditableEntity {
    private static final Set<String> ALLOWED_STATUSES = Set.of("pending", "uploaded", "ready", "missing", "accepted");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long orderId;
    private Long clientAccountId;
    private Long documentTypeId;
    @Column(nullable = false, length = 60)
    private String type;
    @Column(nullable = false, length = 160)
    private String label;
    @Column(nullable = false, length = 40)
    private String status;
    @Column(nullable = false, length = 240)
    private String fileName;
    @Column(nullable = false)
    private boolean visibleToBuyer;
    @Column(nullable = false)
    private boolean required;
    @Column(length = 100)
    private String contentType;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea")
    private byte[] content;

    protected BusinessDocument() { }

    public BusinessDocument(Long tenantId, Long orderId, Long clientAccountId, DocumentType documentType,
                            String label, String fileName, boolean visibleToBuyer, boolean required) {
        if (documentType == null || !documentType.isActive()) throw new IllegalArgumentException("Document type is not active.");
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Tenant id is required.");
        this.tenantId = tenantId;
        this.orderId = orderId;
        this.clientAccountId = clientAccountId;
        this.documentTypeId = documentType.getId();
        this.type = documentType.getKey();
        this.label = require(label, "Document label is required.");
        this.status = "pending";
        this.fileName = optional(fileName);
        this.visibleToBuyer = visibleToBuyer;
        this.required = required;
        this.contentType = "";
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getOrderId() { return orderId; }
    public Long getClientAccountId() { return clientAccountId; }
    public Long getDocumentTypeId() { return documentTypeId; }
    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getStatus() { return status; }
    public String getFileName() { return fileName; }
    public boolean isVisibleToBuyer() { return visibleToBuyer; }
    public boolean isRequired() { return required; }
    public String getContentType() { return contentType; }
    public byte[] getContent() { return content == null ? null : content.clone(); }

    public void attachContent(String fileName, String contentType, byte[] content, boolean visibleToBuyer) {
        if (content == null || content.length == 0) throw new IllegalArgumentException("Document content is required.");
        this.fileName = require(fileName, "Document file name is required.");
        this.contentType = require(contentType, "Document content type is required.");
        this.content = content.clone();
        this.visibleToBuyer = visibleToBuyer;
        this.status = "uploaded";
    }

    public void replaceGeneratedContent(DocumentType documentType, Long clientAccountId, String label,
                                        String fileName, String contentType, byte[] content) {
        if (documentType == null || !documentType.isActive()) throw new IllegalArgumentException("Document type is not active.");
        if (content == null || content.length == 0) throw new IllegalArgumentException("Generated document content is required.");
        this.documentTypeId = documentType.getId();
        this.type = documentType.getKey();
        this.clientAccountId = clientAccountId;
        this.label = require(label, "Document label is required.");
        this.fileName = require(fileName, "Document file name is required.");
        this.contentType = require(contentType, "Document content type is required.");
        this.content = content.clone();
        this.visibleToBuyer = true;
        this.required = true;
        if (!"accepted".equals(status)) changeStatus("ready", true);
    }

    public void changeStatus(String status, Boolean visibleToBuyer) {
        String nextStatus = normalizeStatus(status);
        if ("accepted".equals(this.status) && !"accepted".equals(nextStatus)) {
            throw new IllegalStateException("Accepted business documents cannot move backwards.");
        }
        if ("ready".equals(nextStatus) && !Set.of("pending", "uploaded", "missing", "ready").contains(this.status)) {
            throw new IllegalStateException("Business document cannot be marked ready from its current status.");
        }
        if ("missing".equals(nextStatus) && !required) {
            throw new IllegalStateException("Only required business documents can be marked missing.");
        }
        this.status = nextStatus;
        if (visibleToBuyer != null) this.visibleToBuyer = visibleToBuyer;
    }

    private static String normalizeStatus(String status) {
        String normalized = require(status, "Document status is required.").toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) throw new IllegalArgumentException("Business document status is not supported.");
        return normalized;
    }

    private static String require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private static String optional(String value) {
        return value == null ? "" : value.trim();
    }
}
