package com.nexa.platform.logistics.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class OperationalRecordResponses {
    private OperationalRecordResponses() { }

    public record DispatchEventResponse(Long id, Long tenantId, Long dispatchOrderId, String status, String description,
                                        boolean visibleToBuyer, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record ProofOfDeliveryRecordResponse(Long id, Long tenantId, Long dispatchOrderId, String receivedBy,
                                                OffsetDateTime completedAt, boolean photoReference,
                                                boolean signatureReference, String notes, String status,
                                                OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record TemperatureLogResponse(Long id, Long tenantId, Long dispatchOrderId, Long orderId, BigDecimal celsius,
                                         String zone, String status, OffsetDateTime recordedAt,
                                         OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
}
