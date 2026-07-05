package com.nexa.platform.logistics.application.internal;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class LogisticsMapper {
    public ShipmentResponse toShipmentResponse(Shipment shipment) { return new ShipmentResponse(shipment.getId(), shipment.getOrder().getId(), shipment.getRoute().getName(), shipment.getCarrier(), shipment.getStatus().name(), shipment.getTrackingNote()); }
    public DriverChecklistResponse toChecklistResponse(DriverChecklist checklist) { return new DriverChecklistResponse(checklist.getId(), checklist.getShipment().getId(), checklist.isVehicleClean(), checklist.isTemperatureRecorderReady(), checklist.isSealsVerified(), checklist.getNotes()); }
    public DispatchOrderResponse toDispatchOrderResponse(DispatchOrder dispatchOrder) {
        return new DispatchOrderResponse(dispatchOrder.getId(), dispatchOrder.getTenantId(), dispatchOrder.getOrderId(),
            dispatchOrder.getClientAccountId(), dispatchOrder.getCode(), dispatchOrder.getStatus(), dispatchOrder.getRouteName(),
            dispatchOrder.getResponsible(), dispatchOrder.getEta(), dispatchOrder.getDeliveryWindow(),
            dispatchOrder.getCreatedAt(), dispatchOrder.getUpdatedAt());
    }
    public OperationalRecordResponses.DispatchEventResponse toDispatchEventResponse(DispatchEvent event) {
        return new OperationalRecordResponses.DispatchEventResponse(event.getId(), event.getTenantId(), event.getDispatchOrderId(),
            event.getStatus(), event.getDescription(), event.isVisibleToBuyer(), event.getCreatedAt(), event.getUpdatedAt());
    }
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse toProofOfDeliveryRecordResponse(ProofOfDeliveryRecord proof) {
        return new OperationalRecordResponses.ProofOfDeliveryRecordResponse(proof.getId(), proof.getTenantId(),
            proof.getDispatchOrderId(), proof.getReceivedBy(), proof.getCompletedAt(), proof.isPhotoReference(),
            proof.isSignatureReference(), proof.getNotes(), proof.getStatus(), proof.getCreatedAt(), proof.getUpdatedAt());
    }
    public OperationalRecordResponses.TemperatureLogResponse toTemperatureLogResponse(TemperatureLog log) {
        return new OperationalRecordResponses.TemperatureLogResponse(log.getId(), log.getTenantId(), log.getDispatchOrderId(),
            log.getOrderId(), log.getCelsius(), log.getZone(), log.getStatus(), log.getRecordedAt(), log.getCreatedAt(),
            log.getUpdatedAt());
    }
}
