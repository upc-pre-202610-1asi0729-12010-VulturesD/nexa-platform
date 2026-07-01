package com.nexa.platform.sales.application.dtos;

import java.time.OffsetDateTime;

public final class PurchaseRequestWorkflowDtos {
    private PurchaseRequestWorkflowDtos() { }

    public record OrderAcceptanceResponse(Long purchaseRequestId, Long orderId, Long dispatchOrderId, String status) { }
    public record ReservationRequest(String code, Long inventoryItemId, String productId, String lotCode, int units) { }
    public record ReservationResponse(Long id, String externalId, String status) { }
    public record MessageRequest(Long clientAccountId, Long purchaseRequestId, Long orderId, String senderRole,
                                 String senderName, String body, Boolean visibleToBuyer) { }
    public record MessageResponse(Long id, Long tenantId, Long clientAccountId, Long purchaseRequestId, Long orderId,
                                  String senderRole, String senderName, String body, boolean visibleToBuyer,
                                  OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
