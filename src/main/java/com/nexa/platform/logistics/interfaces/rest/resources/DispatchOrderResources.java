package com.nexa.platform.logistics.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class DispatchOrderResources {
    private DispatchOrderResources() { }

    public record DispatchOrderResource(Long id, Long tenantId, Long orderId, Long clientAccountId, String code,
                                        String status, String routeName, String responsible, OffsetDateTime eta,
                                        String deliveryWindow, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record UpsertDispatchOrderResource(Long tenantId, @NotNull Long orderId, Long clientAccountId,
                                             @NotBlank String code, String status, @NotBlank String routeName,
                                             String responsible, OffsetDateTime eta, String deliveryWindow) { }
    public record CreateDispatchOrderResource(Long clientAccountId, String code, String routeName) { }
    public record AssignDispatchResource(@NotBlank String responsible) { }
    public record ScheduleDispatchResource(@NotNull OffsetDateTime eta, @NotBlank String deliveryWindow, String note) { }
    public record DispatchNoteResource(String note) { }
    public record DispatchStatusChangeResource(@NotBlank String status, String note, Boolean visibleToBuyer) { }
    public record DispatchEventResource(Long id, Long tenantId, Long dispatchOrderId, String status, String description,
                                        boolean visibleToBuyer, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record UpsertDispatchEventResource(Long tenantId, @NotNull Long dispatchOrderId, @NotBlank String status,
                                              @NotBlank String description, Boolean visibleToBuyer) { }
    public record ProofOfDeliveryRecordResource(Long id, Long tenantId, Long dispatchOrderId, String receivedBy,
                                                OffsetDateTime completedAt, boolean photoReference,
                                                boolean signatureReference, String notes, String status,
                                                OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record UpsertProofOfDeliveryRecordResource(Long tenantId, @NotNull Long dispatchOrderId, String receivedBy,
                                                      OffsetDateTime completedAt, Boolean photoReference,
                                                      Boolean signatureReference, String notes, String status) { }
    public record CompletePodResource(@NotBlank String receivedBy, OffsetDateTime completedAt, Boolean photoReference,
                                      Boolean signatureReference, String notes) { }
    public record TemperatureLogResource(Long id, Long tenantId, Long dispatchOrderId, Long orderId, BigDecimal celsius,
                                         String zone, String status, OffsetDateTime recordedAt,
                                         OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
    public record UpsertTemperatureLogResource(Long tenantId, Long dispatchOrderId, Long orderId,
                                               @NotNull BigDecimal celsius, @NotBlank String zone, String status,
                                               OffsetDateTime recordedAt) { }
}
