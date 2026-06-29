package com.nexa.platform.warehouse.application.internal;

import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.sales.domain.model.repositories.PurchaseRequestRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.SalesOrderRepositoryPort;
import com.nexa.platform.warehouse.application.dtos.*;
import com.nexa.platform.warehouse.domain.model.*;
import com.nexa.platform.warehouse.domain.model.repositories.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseService {
    private final WarehouseRepositoryPort warehouses;
    private final InventoryItemRepositoryPort inventory;
    private final InventoryMovementRepositoryPort movements;
    private final StockBatchRepositoryPort lots;
    private final InventoryReservationRepositoryPort reservations;
    private final SalesOrderRepositoryPort orders;
    private final PurchaseRequestRepositoryPort purchaseRequests;
    private final WarehouseMapper mapper;
    public WarehouseService(WarehouseRepositoryPort warehouses, InventoryItemRepositoryPort inventory,
                            InventoryMovementRepositoryPort movements, StockBatchRepositoryPort lots,
                            InventoryReservationRepositoryPort reservations, SalesOrderRepositoryPort orders,
                            PurchaseRequestRepositoryPort purchaseRequests, WarehouseMapper mapper) {
        this.warehouses = warehouses;
        this.inventory = inventory;
        this.movements = movements;
        this.lots = lots;
        this.reservations = reservations;
        this.orders = orders;
        this.purchaseRequests = purchaseRequests;
        this.mapper = mapper;
    }
    public List<WarehouseResponse> listWarehouses() { return warehouses.findAll().stream().map(mapper::toWarehouseResponse).toList(); }
    public List<WarehouseResponse> listWarehouses(Long tenantId) { return warehouses.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream().map(mapper::toWarehouseResponse).toList(); }
    public List<InventoryResponse> listInventory() { return inventory.findAll().stream().map(mapper::toInventoryResponse).toList(); }
    public List<InventoryResponse> listInventory(Long tenantId) { return inventory.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream().map(mapper::toInventoryResponse).toList(); }
    public List<InventoryResponse> listAlerts() { return inventory.findAll().stream().filter(InventoryItem::isLowStock).map(mapper::toInventoryResponse).toList(); }
    public List<InventoryResponse> listAlerts(Long tenantId) { return inventory.findByTenantIdOrderByIdAsc(requireTenant(tenantId)).stream().filter(InventoryItem::isLowStock).map(mapper::toInventoryResponse).toList(); }
    @Transactional
    public MovementResponse registerMovement(MovementRequest request) {
        return registerMovement(1L, request);
    }
    @Transactional
    public MovementResponse registerMovement(Long tenantId, MovementRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        InventoryItem item = resolveTenantInventoryItem(scopedTenantId, request.inventoryItemId(), null);
        MovementType type = MovementType.valueOf(request.type());
        item.apply(request.quantityDelta());
        inventory.save(item);
        return mapper.toMovementResponse(movements.save(new InventoryMovement(scopedTenantId, item,
            "MOV-" + System.currentTimeMillis(), type, request.quantityDelta(), null, item.getWarehouse().getName(),
            null, type.name().toLowerCase(), request.note(), null, "", java.time.OffsetDateTime.now())));
    }
    @Transactional(readOnly = true)
    public List<InventoryOperationDtos.InventoryMovementResponse> listMovements(Long tenantId) {
        return movements.findByTenantIdOrderByOccurredAtDescIdDesc(requireTenant(tenantId)).stream()
            .map(this::toInventoryMovementResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public InventoryOperationDtos.InventoryMovementResponse getMovement(Long tenantId, String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Movement code is required.");
        InventoryMovement movement = movements.findByTenantIdAndCode(requireTenant(tenantId), code.trim().toUpperCase())
            .orElseThrow(() -> new ResourceNotFoundException("Inventory movement", code));
        return toInventoryMovementResponse(movement);
    }
    @Transactional
    public InventoryOperationDtos.InventoryMovementResponse createMovement(
        Long tenantId, InventoryOperationDtos.CreateInventoryMovementRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        InventoryItem item = resolveTenantInventoryItem(scopedTenantId, request.inventoryItemId(), request.productId());
        MovementType type = normalizeMovementType(request.type());
        int signedQuantity = signedQuantity(type, request.quantity());
        int units = Math.abs(request.quantity());
        if (units == 0) throw new IllegalArgumentException("Movement quantity cannot be zero.");
        String code = request.code() == null || request.code().isBlank()
            ? "STM-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase()
            : request.code().trim().toUpperCase();
        if (movements.findByTenantIdAndCode(scopedTenantId, code).isPresent()) {
            throw new IllegalArgumentException("Movement code already exists.");
        }
        StockBatch lot = null;
        if (request.lotCode() != null && !request.lotCode().isBlank()) {
            lot = lots.findByTenantIdAndLotCode(scopedTenantId, request.lotCode().trim()).orElse(null);
            if (lot == null && type == MovementType.ENTRY && signedQuantity > 0) {
                lot = new StockBatch(scopedTenantId, item, request.lotCode(), 0, 0, java.time.LocalDate.now(),
                    request.expirationDate(), "", "active", null, null);
            }
            if (lot == null) throw new ResourceNotFoundException("Inventory lot", request.lotCode());
        }
        if (lot != null && !lot.getItem().getId().equals(item.getId())) {
            throw new IllegalArgumentException("Lot belongs to another inventory item.");
        }
        validateMovementWarehouse(item, request.warehouse());
        String orderReference = validateOrderReference(scopedTenantId, request.orderId());
        applyMovement(item, lot, type, units, signedQuantity);
        inventory.save(item);
        if (lot != null) lots.save(lot);
        InventoryMovement movement = new InventoryMovement(scopedTenantId, item, code, type, signedQuantity,
            lot == null ? request.lotCode() : lot.getLotCode(), item.getWarehouse().getName(), orderReference,
            request.reason(), request.note(), request.temperatureReading(), request.user(), request.occurredAt());
        return toInventoryMovementResponse(movements.save(movement));
    }
    @Transactional(readOnly = true)
    public List<InventoryOperationDtos.InventoryLotResponse> listLots(Long tenantId) {
        requireTenant(tenantId);
        return lots.findByTenantIdOrderByExpirationDateAscIdAsc(tenantId).stream()
            .map(mapper::toInventoryLotResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public InventoryOperationDtos.InventoryLotResponse getLot(Long tenantId, String lotCode) {
        return mapper.toInventoryLotResponse(findLot(requireTenant(tenantId), lotCode));
    }
    @Transactional
    public InventoryOperationDtos.InventoryLotResponse createLot(
        Long tenantId, InventoryOperationDtos.UpsertInventoryLotRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        InventoryItem item = resolveTenantInventoryItem(scopedTenantId, request.inventoryItemId(), request.productId());
        validateWarehouse(item, request.warehouseId(), request.warehouse());
        if (request.lotCode() == null || request.lotCode().isBlank()) {
            throw new IllegalArgumentException("Lot code is required.");
        }
        if (lots.findByTenantIdAndLotCode(scopedTenantId, request.lotCode().trim()).isPresent()) {
            throw new IllegalArgumentException("Lot code already exists.");
        }
        StockBatch lot = new StockBatch(scopedTenantId, item, request.lotCode(), request.quantity(),
            request.reservedQuantity(), request.entryDate(), request.expirationDate(), request.zone(), request.status(),
            request.minimumTemperature(), request.maximumTemperature());
        return mapper.toInventoryLotResponse(lots.save(lot));
    }
    @Transactional
    public InventoryOperationDtos.InventoryLotResponse updateLot(
        Long tenantId, Long id, InventoryOperationDtos.UpsertInventoryLotRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        StockBatch lot = lots.findByIdAndTenantId(id, scopedTenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory lot", id));
        InventoryItem item = request.inventoryItemId() == null && (request.productId() == null || request.productId().isBlank())
            ? lot.getItem()
            : resolveTenantInventoryItem(scopedTenantId, request.inventoryItemId(), request.productId());
        validateWarehouse(item, request.warehouseId(), request.warehouse());
        String lotCode = request.lotCode() == null || request.lotCode().isBlank() ? lot.getLotCode() : request.lotCode();
        lots.findByTenantIdAndLotCode(scopedTenantId, lotCode.trim())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> { throw new IllegalArgumentException("Lot code already exists."); });
        int quantity = request.quantity() == 0 ? lot.getQuantity() : request.quantity();
        int reserved = request.reservedQuantity() == 0 ? lot.getReservedQuantity() : request.reservedQuantity();
        lot.update(scopedTenantId, item, lotCode, quantity, reserved,
            request.entryDate() == null ? lot.getEntryDate() : request.entryDate(),
            request.expirationDate() == null ? lot.getExpirationDate() : request.expirationDate(),
            request.zone() == null ? lot.getZone() : request.zone(),
            request.status() == null ? lot.getStatus() : request.status(),
            request.minimumTemperature() == null ? lot.getMinimumTemperature() : request.minimumTemperature(),
            request.maximumTemperature() == null ? lot.getMaximumTemperature() : request.maximumTemperature());
        return mapper.toInventoryLotResponse(lots.save(lot));
    }
    @Transactional(readOnly = true)
    public List<InventoryOperationDtos.InventoryReservationResponse> listReservations(Long tenantId) {
        requireTenant(tenantId);
        return reservations.findByTenantIdOrderByIdDesc(tenantId).stream()
            .map(mapper::toInventoryReservationResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public InventoryOperationDtos.InventoryReservationResponse getReservation(Long tenantId, Long id) {
        return mapper.toInventoryReservationResponse(findReservation(requireTenant(tenantId), id));
    }
    @Transactional
    public InventoryOperationDtos.InventoryReservationResponse createReservation(
        Long tenantId, InventoryOperationDtos.CreateInventoryReservationRequest request) {
        Long scopedTenantId = requireTenant(tenantId);
        InventoryItem item = resolveTenantInventoryItem(scopedTenantId, request.inventoryItemId(), request.productId());
        if (request.units() <= 0) throw new IllegalArgumentException("Reservation units must be positive.");
        String code = request.code() == null || request.code().isBlank()
            ? "RES-" + System.currentTimeMillis()
            : request.code().trim().toUpperCase();
        if (reservations.existsByTenantIdAndInventoryItemIdAndCodeAndStatus(
            scopedTenantId, item.getId(), code, "reserved")) {
            throw new IllegalArgumentException("Active reservation code already exists.");
        }
        StockBatch lot = request.lotCode() == null || request.lotCode().isBlank()
            ? null
            : findLot(scopedTenantId, request.lotCode());
        if (lot != null && !lot.getItem().getId().equals(item.getId())) {
            throw new IllegalArgumentException("Lot belongs to another inventory item.");
        }
        Long orderId = resolveOrderId(scopedTenantId, request.orderId());
        Long purchaseRequestId = resolvePurchaseRequestId(request.purchaseRequestId());
        item.reserve(request.units());
        if (lot != null) lot.reserve(request.units());
        inventory.save(item);
        if (lot != null) lots.save(lot);
        InventoryReservationRecord reservation = new InventoryReservationRecord(scopedTenantId, item, lot, orderId,
            purchaseRequestId, code, request.units());
        return mapper.toInventoryReservationResponse(reservations.save(reservation));
    }
    @Transactional
    public InventoryOperationDtos.InventoryReservationResponse releaseReservation(Long tenantId, Long id) {
        Long scopedTenantId = requireTenant(tenantId);
        InventoryReservationRecord reservation = findReservation(scopedTenantId, id);
        if (reservation.release()) {
            reservation.getInventoryItem().release(reservation.getUnits());
            inventory.save(reservation.getInventoryItem());
            if (reservation.getInventoryLot() != null) {
                reservation.getInventoryLot().release(reservation.getUnits());
                lots.save(reservation.getInventoryLot());
            }
            reservations.save(reservation);
        }
        return mapper.toInventoryReservationResponse(reservation);
    }

    private InventoryItem resolveInventoryItem(Long id, String productId) {
        if (id != null) {
            return inventory.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory item", id));
        }
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product or inventory item is required.");
        }
        return inventory.findAll().stream()
            .filter(item -> item.getProduct().getSku().equalsIgnoreCase(productId.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Inventory item was not found for the product."));
    }

    private InventoryItem resolveTenantInventoryItem(Long tenantId, Long id, String productId) {
        return inventory.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(item -> id != null ? id.equals(item.getId())
                : productId != null && item.getProduct().getSku().equalsIgnoreCase(productId.trim()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Inventory item", id == null ? productId : id));
    }

    private void validateWarehouse(InventoryItem item, Long warehouseId, String warehouse) {
        if (warehouseId != null && !warehouseId.equals(item.getWarehouse().getId())) {
            throw new IllegalArgumentException("Warehouse does not belong to the inventory item.");
        }
        if (warehouse != null && !warehouse.isBlank()
            && !warehouse.equalsIgnoreCase(item.getWarehouse().getName())) {
            throw new IllegalArgumentException("Warehouse does not belong to the inventory item.");
        }
    }

    private StockBatch findLot(Long tenantId, String lotCode) {
        if (lotCode == null || lotCode.isBlank()) throw new IllegalArgumentException("Lot code is required.");
        return lots.findByTenantIdAndLotCode(tenantId, lotCode.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Inventory lot", lotCode));
    }

    private InventoryReservationRecord findReservation(Long tenantId, Long id) {
        return reservations.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory reservation", id));
    }

    private Long resolveOrderId(Long tenantId, String orderReference) {
        if (orderReference == null || orderReference.isBlank()) return null;
        String value = orderReference.trim();
        int separator = value.lastIndexOf('-');
        String numeric = separator >= 0 ? value.substring(separator + 1) : value;
        try {
            Long id = Long.valueOf(numeric);
            orders.findByIdAndTenantId(id, tenantId).orElseThrow(() -> new IllegalArgumentException("Order was not found."));
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Order reference is invalid.");
        }
    }

    private String validateOrderReference(Long tenantId, String orderReference) {
        if (orderReference == null || orderReference.isBlank()) return "";
        Long id = resolveOrderId(tenantId, orderReference);
        return "ORD-2026-" + String.format("%04d", id);
    }

    private void validateMovementWarehouse(InventoryItem item, String warehouse) {
        if (warehouse != null && !warehouse.isBlank() && !warehouse.equalsIgnoreCase(item.getWarehouse().getName())) {
            throw new IllegalArgumentException("Warehouse does not belong to the inventory item.");
        }
    }

    private void applyMovement(InventoryItem item, StockBatch lot, MovementType type, int units, int signedQuantity) {
        switch (type) {
            case ENTRY, EXIT, ADJUSTMENT, RECEIPT, PICKING, DISPATCH -> item.apply(signedQuantity);
            case RESERVATION -> item.reserve(units);
            case RESERVATION_RELEASE -> item.release(units);
            case REVIEW -> { }
        }
        if (lot == null) return;
        switch (type) {
            case RESERVATION -> lot.reserve(units);
            case RESERVATION_RELEASE -> lot.release(units);
            case ENTRY, EXIT, ADJUSTMENT, RECEIPT, PICKING, DISPATCH -> lot.update(lot.getTenantId(), lot.getItem(),
                lot.getLotCode(), lot.getQuantity() + signedQuantity, lot.getReservedQuantity(), lot.getEntryDate(),
                lot.getExpirationDate(), lot.getZone(), lot.getStatus(), lot.getMinimumTemperature(), lot.getMaximumTemperature());
            case REVIEW -> { }
        }
    }

    private MovementType normalizeMovementType(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Movement type is required.");
        return switch (value.trim().toLowerCase().replace('-', '_')) {
            case "entry", "inbound", "ingreso", "receipt" -> MovementType.ENTRY;
            case "exit", "outbound", "salida", "dispatch" -> MovementType.EXIT;
            case "adjustment", "ajuste" -> MovementType.ADJUSTMENT;
            case "reservation", "reserva" -> MovementType.RESERVATION;
            case "reservation_release", "release" -> MovementType.RESERVATION_RELEASE;
            case "review" -> MovementType.REVIEW;
            default -> throw new IllegalArgumentException("Unsupported inventory movement type.");
        };
    }

    private int signedQuantity(MovementType type, int rawQuantity) {
        return switch (type) {
            case ENTRY, RECEIPT, RESERVATION_RELEASE -> Math.abs(rawQuantity);
            case EXIT, PICKING, DISPATCH, RESERVATION -> -Math.abs(rawQuantity);
            case ADJUSTMENT, REVIEW -> rawQuantity;
        };
    }

    private InventoryOperationDtos.InventoryMovementResponse toInventoryMovementResponse(InventoryMovement movement) {
        String type = switch (movement.getType()) {
            case ENTRY, RECEIPT -> "entry";
            case EXIT, PICKING, DISPATCH -> "exit";
            case ADJUSTMENT -> "adjustment";
            case RESERVATION -> "reservation";
            case RESERVATION_RELEASE -> "reservation_release";
            case REVIEW -> "review";
        };
        return new InventoryOperationDtos.InventoryMovementResponse(movement.getId(), movement.getTenantId(),
            movement.getCode(), movement.getCode(), movement.getItem().getId(), movement.getItem().getProduct().getSku(),
            movement.getLotCode(), movement.getWarehouse(), type, movement.getQuantityDelta(),
            movement.getOrderReference(), movement.getReason(), movement.getNote(), movement.getTemperatureReading(),
            movement.getPerformedBy(), movement.getOccurredAt(), movement.getCreatedAt());
    }

    private Long resolvePurchaseRequestId(Long id) {
        if (id == null) return null;
        boolean exists = purchaseRequests.findAllByOrderByIdAsc().stream().anyMatch(request -> id.equals(request.getId()));
        if (!exists) throw new IllegalArgumentException("Purchase request was not found.");
        return id;
    }

    private static Long requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
        return tenantId;
    }
}
