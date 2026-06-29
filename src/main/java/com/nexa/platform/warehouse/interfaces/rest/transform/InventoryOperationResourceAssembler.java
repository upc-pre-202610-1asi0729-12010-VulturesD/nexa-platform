package com.nexa.platform.warehouse.interfaces.rest.transform;

import com.nexa.platform.warehouse.application.dtos.InventoryOperationDtos;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryOperationResources.*;

public final class InventoryOperationResourceAssembler {
    private InventoryOperationResourceAssembler() { }

    public static InventoryLotResource toResource(InventoryOperationDtos.InventoryLotResponse response) {
        return new InventoryLotResource(response.backendId(), response.tenantId(), response.inventoryItemId(),
            response.productId(), response.catalogItemId(), response.warehouseId(), response.warehouse(),
            response.id(), response.lotCode(), response.qty(), response.reserved(), response.entryDate(),
            response.expiry(), response.zone(), response.status(), response.minimumTemperature(),
            response.maximumTemperature(), response.createdAt(), response.updatedAt());
    }

    public static InventoryOperationDtos.UpsertInventoryLotRequest toRequest(UpsertInventoryLotResource resource) {
        return new InventoryOperationDtos.UpsertInventoryLotRequest(resource.inventoryItemId(), resource.productId(),
            resource.warehouseId(), resource.warehouse(), first(resource.lotCode(), resource.id()),
            number(resource.qty(), resource.quantity()), number(resource.reserved(), resource.reservedQuantity()),
            resource.entryDate(), resource.expiry() == null ? resource.expirationDate() : resource.expiry(),
            resource.zone(), resource.status(), resource.minimumTemperature(), resource.maximumTemperature());
    }

    public static InventoryReservationResource toResource(
        InventoryOperationDtos.InventoryReservationResponse response) {
        return new InventoryReservationResource(response.id(), response.tenantId(), response.code(),
            response.inventoryItemId(), response.productId(), response.lotCode(), response.orderId(),
            response.purchaseRequestId(), response.units(), response.status(), response.createdAt(),
            response.updatedAt());
    }

    public static InventoryOperationDtos.CreateInventoryReservationRequest toRequest(
        CreateInventoryReservationResource resource) {
        return new InventoryOperationDtos.CreateInventoryReservationRequest(first(resource.code(), resource.id()),
            resource.inventoryItemId(), resource.productId(), first(resource.lotCode(), resource.lotId()),
            resource.orderId(), resource.purchaseRequestId(), number(resource.quantity(), resource.units()));
    }

    public static InventoryMovementResource toResource(InventoryOperationDtos.InventoryMovementResponse response) {
        return new InventoryMovementResource(response.backendId(), response.tenantId(), response.id(), response.code(),
            response.inventoryItemId(), response.productId(), response.lotId(), response.warehouse(), response.type(),
            response.qty(), response.orderId(), response.reason(), response.note(), response.temperatureReading(),
            response.user(), response.date(), response.createdAt());
    }

    public static InventoryOperationDtos.CreateInventoryMovementRequest toRequest(
        CreateInventoryMovementResource resource) {
        return new InventoryOperationDtos.CreateInventoryMovementRequest(
            first(resource.code(), resource.id()), resource.inventoryItemId(), resource.productId(),
            resource.warehouse(), first(resource.lotId(), resource.lotNumber()),
            first(resource.type(), resource.movementType()), number(resource.qty(), resource.quantity()),
            first(resource.orderId(), resource.reference()), resource.reason(), first(resource.note(), resource.notes()),
            resource.temperatureReading(), resource.user(), resource.occurredAt(), resource.expirationDate());
    }

    private static String first(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private static int number(Integer preferred, Integer fallback) {
        return preferred == null ? (fallback == null ? 0 : fallback) : preferred;
    }
}
