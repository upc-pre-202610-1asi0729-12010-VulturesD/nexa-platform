package com.nexa.platform.logistics.application.internal;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.domain.model.*;
import com.nexa.platform.logistics.domain.model.repositories.*;
import com.nexa.platform.sales.domain.model.SalesOrder;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogisticsService {
    private final ShipmentRepositoryPort shipments;
    private final DispatchOrderRepositoryPort dispatchOrders;
    private final DispatchEventRepositoryPort dispatchEvents;
    private final ProofOfDeliveryRecordRepositoryPort proofsOfDelivery;
    private final TemperatureLogRepositoryPort temperatureLogs;
    private final DeliveryRouteRepositoryPort routes;
    private final DriverChecklistRepositoryPort checklists;
    private final SalesOrderRepositoryPort orders;
    private final LogisticsMapper mapper;
    public LogisticsService(ShipmentRepositoryPort shipments, DispatchOrderRepositoryPort dispatchOrders,
                            DispatchEventRepositoryPort dispatchEvents, ProofOfDeliveryRecordRepositoryPort proofsOfDelivery,
                            TemperatureLogRepositoryPort temperatureLogs, DeliveryRouteRepositoryPort routes,
                            DriverChecklistRepositoryPort checklists, SalesOrderRepositoryPort orders, LogisticsMapper mapper) { this.shipments = shipments; this.dispatchOrders = dispatchOrders; this.dispatchEvents = dispatchEvents; this.proofsOfDelivery = proofsOfDelivery; this.temperatureLogs = temperatureLogs; this.routes = routes; this.checklists = checklists; this.orders = orders; this.mapper = mapper; }
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
    @Transactional(readOnly = true)
    public List<DispatchOrderResponse> listDispatchOrders() {
        return dispatchOrders.findAllByOrderByIdAsc().stream().map(mapper::toDispatchOrderResponse).toList();
    }
    @Transactional(readOnly = true)
    public DispatchOrderResponse getDispatchOrder(Long id) {
        return mapper.toDispatchOrderResponse(findDispatchOrder(id));
    }
    @Transactional(readOnly = true)
    public DispatchOrderResponse getDispatchOrderForOrder(Long orderId) {
        return mapper.toDispatchOrderResponse(dispatchOrders.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Dispatch order", "order " + orderId)));
    }
    @Transactional
    public DispatchOrderResponse createDispatchOrder(UpsertDispatchOrderRequest request) {
        SalesOrder order = orders.findById(request.orderId()).orElseThrow(() -> new ResourceNotFoundException("Sales order", request.orderId()));
        if (!order.getTenantId().equals(request.tenantId())) {
            throw new ResourceNotFoundException("Sales order", request.orderId());
        }
        Long clientAccountId = request.clientAccountId() == null ? order.getCustomer().getId() : request.clientAccountId();
        if (dispatchOrders.existsByCode(request.code())) throw new IllegalArgumentException("Dispatch code already exists.");
        DispatchOrder dispatchOrder = new DispatchOrder(request.tenantId(), request.orderId(), clientAccountId,
            request.code(), request.status(), request.routeName(), request.responsible(), request.eta(), request.deliveryWindow());
        return mapper.toDispatchOrderResponse(dispatchOrders.save(dispatchOrder));
    }
    @Transactional
    public DispatchOrderResponse updateDispatchOrder(Long id, UpsertDispatchOrderRequest request) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.update(request.tenantId(), request.orderId(), request.clientAccountId(), request.code(), request.status(),
            request.routeName(), request.responsible(), request.eta(), request.deliveryWindow());
        return mapper.toDispatchOrderResponse(dispatchOrders.save(dispatchOrder));
    }
    @Transactional
    public void deleteDispatchOrder(Long id) {
        dispatchOrders.delete(findDispatchOrder(id));
    }
    @Transactional
    public DispatchOrderResponse assignDispatchOrder(Long id, String responsible) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.assign(responsible);
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "assigned", "Dispatch assigned to " + responsible, false);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse scheduleDispatchOrder(Long id, java.time.OffsetDateTime eta, String deliveryWindow, String note) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.schedule(eta, deliveryWindow, "scheduled");
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "scheduled", description(note, "Dispatch scheduled for " + deliveryWindow), true);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse startDispatchRoute(Long id) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.startRoute();
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "in_route", "Dispatch route started", true);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse completeDispatchDelivery(Long id) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.complete();
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "delivered", "Dispatch delivered", true);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse incidentDispatchOrder(Long id, String note) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.incident();
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "incident", description(note, "Dispatch incident reported"), true);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse rescheduleDispatchOrder(Long id, java.time.OffsetDateTime eta, String deliveryWindow, String note) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.reschedule(eta, deliveryWindow);
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, "reprogrammed", description(note, "Dispatch rescheduled for " + deliveryWindow), true);
        return mapper.toDispatchOrderResponse(saved);
    }
    @Transactional
    public DispatchOrderResponse changeDispatchStatus(Long id, String status, String note, Boolean visibleToBuyer) {
        DispatchOrder dispatchOrder = findDispatchOrder(id);
        dispatchOrder.changeStatus(status);
        DispatchOrder saved = dispatchOrders.save(dispatchOrder);
        recordDispatchEvent(saved, status, description(note, "Dispatch status changed to " + status), visibleToBuyer);
        return mapper.toDispatchOrderResponse(saved);
    }

    private void recordDispatchEvent(DispatchOrder dispatchOrder, String status, String description, Boolean visibleToBuyer) {
        dispatchEvents.save(new DispatchEvent(dispatchOrder.getTenantId(), dispatchOrder.getId(), status, description, visibleToBuyer));
    }

    private String description(String note, String fallback) {
        return note == null || note.isBlank() ? fallback : note.trim();
    }

    private DispatchOrder findDispatchOrder(Long id) {
        return dispatchOrders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dispatch order", id));
    }

    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.DispatchEventResponse> listDispatchEvents() {
        return dispatchEvents.findAllByOrderByIdAsc().stream().map(mapper::toDispatchEventResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.DispatchEventResponse> listDispatchEventsByDispatch(Long dispatchOrderId) {
        findDispatchOrder(dispatchOrderId);
        return dispatchEvents.findByDispatchOrderIdOrderByIdAsc(dispatchOrderId).stream().map(mapper::toDispatchEventResponse).toList();
    }
    @Transactional(readOnly = true)
    public OperationalRecordResponses.DispatchEventResponse getDispatchEvent(Long id) {
        return mapper.toDispatchEventResponse(findDispatchEvent(id));
    }
    @Transactional
    public OperationalRecordResponses.DispatchEventResponse createDispatchEvent(OperationalRecordRequests.UpsertDispatchEventRequest request) {
        findDispatchOrder(request.dispatchOrderId());
        DispatchEvent event = new DispatchEvent(request.tenantId(), request.dispatchOrderId(), request.status(), request.description(), request.visibleToBuyer());
        return mapper.toDispatchEventResponse(dispatchEvents.save(event));
    }
    @Transactional
    public OperationalRecordResponses.DispatchEventResponse updateDispatchEvent(Long id, OperationalRecordRequests.UpsertDispatchEventRequest request) {
        DispatchEvent event = findDispatchEvent(id);
        event.update(request.tenantId(), request.dispatchOrderId(), request.status(), request.description(), request.visibleToBuyer());
        return mapper.toDispatchEventResponse(dispatchEvents.save(event));
    }
    @Transactional
    public void deleteDispatchEvent(Long id) {
        dispatchEvents.delete(findDispatchEvent(id));
    }

    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.ProofOfDeliveryRecordResponse> listProofsOfDelivery() {
        return proofsOfDelivery.findAllByOrderByIdAsc().stream().map(mapper::toProofOfDeliveryRecordResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.ProofOfDeliveryRecordResponse> listProofsOfDeliveryByDispatch(Long dispatchOrderId) {
        findDispatchOrder(dispatchOrderId);
        return proofsOfDelivery.findByDispatchOrderIdOrderByIdAsc(dispatchOrderId).stream().map(mapper::toProofOfDeliveryRecordResponse).toList();
    }
    @Transactional(readOnly = true)
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse getProofOfDelivery(Long id) {
        return mapper.toProofOfDeliveryRecordResponse(findProofOfDelivery(id));
    }
    @Transactional
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse createProofOfDelivery(OperationalRecordRequests.UpsertProofOfDeliveryRecordRequest request) {
        findDispatchOrder(request.dispatchOrderId());
        ProofOfDeliveryRecord proof = new ProofOfDeliveryRecord(request.tenantId(), request.dispatchOrderId(), request.receivedBy(),
            request.completedAt(), request.photoReference(), request.signatureReference(), request.notes(), request.status());
        return mapper.toProofOfDeliveryRecordResponse(proofsOfDelivery.save(proof));
    }
    @Transactional
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse createProofOfDeliveryForDispatch(Long dispatchOrderId, OperationalRecordRequests.CompletePodRequest request) {
        DispatchOrder dispatchOrder = findDispatchOrder(dispatchOrderId);
        ProofOfDeliveryRecord proof = new ProofOfDeliveryRecord(dispatchOrder.getTenantId(), dispatchOrderId, request.receivedBy(),
            request.completedAt(), request.photoReference(), request.signatureReference(), request.notes(), "completed");
        return mapper.toProofOfDeliveryRecordResponse(proofsOfDelivery.save(proof));
    }
    @Transactional
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse completeProofOfDelivery(Long id, OperationalRecordRequests.CompletePodRequest request) {
        ProofOfDeliveryRecord proof = findProofOfDelivery(id);
        proof.complete(request.receivedBy(), request.completedAt(), request.photoReference(), request.signatureReference(), request.notes());
        return mapper.toProofOfDeliveryRecordResponse(proofsOfDelivery.save(proof));
    }
    @Transactional
    public OperationalRecordResponses.ProofOfDeliveryRecordResponse updateProofOfDelivery(Long id, OperationalRecordRequests.UpsertProofOfDeliveryRecordRequest request) {
        ProofOfDeliveryRecord proof = findProofOfDelivery(id);
        proof.update(request.tenantId(), request.dispatchOrderId(), request.receivedBy(), request.completedAt(), request.photoReference(), request.signatureReference(), request.notes(), request.status());
        return mapper.toProofOfDeliveryRecordResponse(proofsOfDelivery.save(proof));
    }
    @Transactional
    public void deleteProofOfDelivery(Long id) {
        proofsOfDelivery.delete(findProofOfDelivery(id));
    }

    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.TemperatureLogResponse> listTemperatureLogs() {
        return temperatureLogs.findAllByOrderByIdAsc().stream().map(mapper::toTemperatureLogResponse).toList();
    }
    @Transactional(readOnly = true)
    public List<OperationalRecordResponses.TemperatureLogResponse> listTemperatureLogsByDispatch(Long dispatchOrderId) {
        findDispatchOrder(dispatchOrderId);
        return temperatureLogs.findByDispatchOrderIdOrderByIdAsc(dispatchOrderId).stream().map(mapper::toTemperatureLogResponse).toList();
    }
    @Transactional(readOnly = true)
    public OperationalRecordResponses.TemperatureLogResponse getTemperatureLog(Long id) {
        return mapper.toTemperatureLogResponse(findTemperatureLog(id));
    }
    @Transactional
    public OperationalRecordResponses.TemperatureLogResponse createTemperatureLog(OperationalRecordRequests.UpsertTemperatureLogRequest request) {
        if (request.dispatchOrderId() != null) findDispatchOrder(request.dispatchOrderId());
        TemperatureLog log = new TemperatureLog(request.tenantId(), request.dispatchOrderId(), request.orderId(), request.celsius(),
            request.zone(), request.status(), request.recordedAt());
        return mapper.toTemperatureLogResponse(temperatureLogs.save(log));
    }
    @Transactional
    public OperationalRecordResponses.TemperatureLogResponse createTemperatureLogForDispatch(Long dispatchOrderId, OperationalRecordRequests.UpsertTemperatureLogRequest request) {
        DispatchOrder dispatchOrder = findDispatchOrder(dispatchOrderId);
        TemperatureLog log = new TemperatureLog(dispatchOrder.getTenantId(), dispatchOrderId, dispatchOrder.getOrderId(),
            request.celsius(), request.zone(), request.status(), request.recordedAt());
        return mapper.toTemperatureLogResponse(temperatureLogs.save(log));
    }
    @Transactional
    public OperationalRecordResponses.TemperatureLogResponse updateTemperatureLog(Long id, OperationalRecordRequests.UpsertTemperatureLogRequest request) {
        TemperatureLog log = findTemperatureLog(id);
        log.update(request.tenantId(), request.dispatchOrderId(), request.orderId(), request.celsius(), request.zone(), request.status(), request.recordedAt());
        return mapper.toTemperatureLogResponse(temperatureLogs.save(log));
    }
    @Transactional
    public OperationalRecordResponses.TemperatureLogResponse resolveTemperatureAlert(Long id) {
        TemperatureLog log = findTemperatureLog(id);
        log.resolveAlert();
        return mapper.toTemperatureLogResponse(temperatureLogs.save(log));
    }
    @Transactional
    public void deleteTemperatureLog(Long id) {
        temperatureLogs.delete(findTemperatureLog(id));
    }

    private DispatchEvent findDispatchEvent(Long id) {
        return dispatchEvents.findById(id).orElseThrow(() -> new ResourceNotFoundException("Dispatch event", id));
    }

    private ProofOfDeliveryRecord findProofOfDelivery(Long id) {
        return proofsOfDelivery.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proof of delivery", id));
    }

    private TemperatureLog findTemperatureLog(Long id) {
        return temperatureLogs.findById(id).orElseThrow(() -> new ResourceNotFoundException("Temperature log", id));
    }
}
