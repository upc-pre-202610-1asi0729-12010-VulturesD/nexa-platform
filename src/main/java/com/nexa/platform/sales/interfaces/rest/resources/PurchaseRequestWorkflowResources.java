package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public final class PurchaseRequestWorkflowResources {
    private PurchaseRequestWorkflowResources() { }

    public record OrderAcceptanceResource(Long purchaseRequestId, Long orderId, Long dispatchOrderId, String status) { }
    public record ReservationRequestResource(String id, Long inventoryItemId, String productId, String lotCode,
                                             @Min(1) int units) { }
    public record ReservationResource(Long id, String externalId, String status) { }
    public record PurchaseRequestMessageResource(@NotBlank String body, String senderRole, String senderName,
                                                 Boolean visibleToBuyer) { }
    public record UpsertConversationMessageResource(Long clientAccountId, Long purchaseRequestId, Long orderId,
                                                    String senderRole, String senderName, @NotBlank String body,
                                                    Boolean visibleToBuyer) { }
    public record ConversationMessageResource(Long id, Long tenantId, Long clientAccountId, Long purchaseRequestId,
                                              Long orderId, String senderRole, String senderName, String body,
                                              boolean visibleToBuyer, OffsetDateTime createdAt,
                                              OffsetDateTime updatedAt) { }
}
