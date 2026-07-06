package com.nexa.platform.logistics.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class OperationalRecordRequests {
    private OperationalRecordRequests() { }

    public record UpsertDispatchEventRequest(Long tenantId, Long dispatchOrderId, String status, String description,
                                             Boolean visibleToBuyer) { }
    public record UpsertProofOfDeliveryRecordRequest(Long tenantId, Long dispatchOrderId, String receivedBy,
                                                     OffsetDateTime completedAt, Boolean photoReference,
                                                     Boolean signatureReference, String notes, String status) { }
    public record CompletePodRequest(String receivedBy, OffsetDateTime completedAt, Boolean photoReference,
                                     Boolean signatureReference, String notes) { }
    public record UpsertTemperatureLogRequest(Long tenantId, Long dispatchOrderId, Long orderId, BigDecimal celsius,
                                              String zone, String status, OffsetDateTime recordedAt) { }
}
