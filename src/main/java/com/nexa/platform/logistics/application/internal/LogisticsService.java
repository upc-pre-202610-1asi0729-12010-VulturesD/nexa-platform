package com.nexa.platform.logistics.application.internal;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.domain.model.*;
import com.nexa.platform.logistics.infrastructure.persistence.jpa.*;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.infrastructure.persistence.jpa.SalesOrderRepository;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogisticsService {
    private final ShipmentRepository shipments;
    private final DeliveryRouteRepository routes;
    private final DriverChecklistRepository checklists;
    private final SalesOrderRepository orders;
    private final LogisticsMapper mapper;
    public LogisticsService(ShipmentRepository shipments, DeliveryRouteRepository routes, DriverChecklistRepository checklists, SalesOrderRepository orders, LogisticsMapper mapper) { this.shipments = shipments; this.routes = routes; this.checklists = checklists; this.orders = orders; this.mapper = mapper; }
    @Transactional public ShipmentResponse createShipment(CreateShipmentRequest request) {
        SalesOrder order = orders.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Sales order", request.orderId()));
        DeliveryRoute route = routes.findById(request.routeId()).orElseThrow(() -> new ResourceNotFoundException("Delivery route", request.routeId()));
        return mapper.toShipmentResponse(shipments.save(new Shipment(order, route, request.carrier(), request.trackingNote())));
    }
    public List<ShipmentResponse> listShipments() { return shipments.findAll().stream().map(mapper::toShipmentResponse).toList(); }
    public ShipmentResponse tracking(Long id) { return mapper.toShipmentResponse(shipments.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shipment", id))); }
    @Transactional public DriverChecklistResponse createChecklist(DriverChecklistRequest request) {
        Shipment shipment = shipments.findById(request.shipmentId()).orElseThrow(() -> new ResourceNotFoundException("Shipment", request.shipmentId()));
        return mapper.toChecklistResponse(checklists.save(new DriverChecklist(shipment, request.vehicleClean(), request.temperatureRecorderReady(), request.sealsVerified(), request.notes())));
    }
}
