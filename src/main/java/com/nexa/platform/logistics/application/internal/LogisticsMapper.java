package com.nexa.platform.logistics.application.internal;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class LogisticsMapper {
    public ShipmentResponse toShipmentResponse(Shipment shipment) { return new ShipmentResponse(shipment.getId(), shipment.getOrder().getId(), shipment.getRoute().getName(), shipment.getCarrier(), shipment.getStatus().name(), shipment.getTrackingNote()); }
    public DriverChecklistResponse toChecklistResponse(DriverChecklist checklist) { return new DriverChecklistResponse(checklist.getId(), checklist.getShipment().getId(), checklist.isVehicleClean(), checklist.isTemperatureRecorderReady(), checklist.isSealsVerified(), checklist.getNotes()); }
}
