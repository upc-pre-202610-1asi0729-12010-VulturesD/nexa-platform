package com.nexa.platform.sales.domain.model;

import com.nexa.platform.shared.domain.model.AuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "sales_conversation_messages",
    indexes = {
        @Index(name = "idx_sales_messages_tenant", columnList = "tenant_id"),
        @Index(name = "idx_sales_messages_request", columnList = "purchase_request_id")
    })
public class ConversationMessage extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long tenantId;
    private Long clientAccountId;
    private Long purchaseRequestId;
    private Long orderId;
    @Column(nullable = false, length = 40)
    private String senderRole;
    @Column(nullable = false, length = 120)
    private String senderName;
    @Column(nullable = false, length = 1200)
    private String body;
    @Column(nullable = false)
    private boolean visibleToBuyer;

    protected ConversationMessage() { }

    public ConversationMessage(Long tenantId, Long clientAccountId, Long purchaseRequestId, Long orderId,
                               String senderRole, String senderName, String body, Boolean visibleToBuyer) {
        this.tenantId = requireId(tenantId, "Current tenant is required.");
        update(clientAccountId, purchaseRequestId, orderId, senderRole, senderName, body, visibleToBuyer);
    }

    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getClientAccountId() { return clientAccountId; }
    public Long getPurchaseRequestId() { return purchaseRequestId; }
    public Long getOrderId() { return orderId; }
    public String getSenderRole() { return senderRole; }
    public String getSenderName() { return senderName; }
    public String getBody() { return body; }
    public boolean isVisibleToBuyer() { return visibleToBuyer; }

    public void update(Long clientAccountId, Long purchaseRequestId, Long orderId, String senderRole,
                       String senderName, String body, Boolean visibleToBuyer) {
        this.clientAccountId = clientAccountId;
        this.purchaseRequestId = purchaseRequestId;
        this.orderId = orderId;
        this.senderRole = normalizeRole(senderRole);
        this.senderName = senderName == null ? "" : senderName.trim();
        this.body = requireText(body, "Message body is required.");
        this.visibleToBuyer = visibleToBuyer == null || visibleToBuyer;
    }

    private static String normalizeRole(String value) {
        String role = value == null || value.isBlank() ? "commercial" : value.trim().toLowerCase();
        if (!java.util.Set.of("buyer", "commercial", "sales", "logistics", "system").contains(role)) {
            throw new IllegalArgumentException("Message sender role is not supported.");
        }
        return role;
    }

    private static Long requireId(Long value, String message) {
        if (value == null || value <= 0) throw new IllegalArgumentException(message);
        return value;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }
}
