package com.nexa.platform.logistics.interfaces.rest.transform;

import com.nexa.platform.logistics.application.dtos.DispatchOrderResponse;
import com.nexa.platform.logistics.application.dtos.OperationalRecordRequests;
import com.nexa.platform.logistics.application.dtos.OperationalRecordResponses;
import com.nexa.platform.logistics.application.dtos.UpsertDispatchOrderRequest;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.CompletePodResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.DispatchOrderResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.DispatchEventResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.ProofOfDeliveryRecordResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.TemperatureLogResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertDispatchOrderResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertDispatchEventResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertProofOfDeliveryRecordResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertTemperatureLogResource;

public final class DispatchOrderResourceAssembler {
    private DispatchOrderResourceAssembler() { }

    public static DispatchOrderResource toResource(DispatchOrderResponse response) {
        return new DispatchOrderResource(response.id(), response.tenantId(), response.orderId(), response.clientAccountId(),
            response.code(), response.status(), response.routeName(), response.responsible(), response.eta(),
            response.deliveryWindow(), response.createdAt(), response.updatedAt());
    }

    public static UpsertDispatchOrderRequest toRequest(UpsertDispatchOrderResource resource) {
        return new UpsertDispatchOrderRequest(resource.tenantId(), resource.orderId(), resource.clientAccountId(),
            resource.code(), resource.status(), resource.routeName(), resource.responsible(), resource.eta(),
            resource.deliveryWindow());
    }

    public static DispatchEventResource toResource(OperationalRecordResponses.DispatchEventResponse response) {
        return new DispatchEventResource(response.id(), response.tenantId(), response.dispatchOrderId(), response.status(),
            response.description(), response.visibleToBuyer(), response.createdAt(), response.updatedAt());
    }

    public static OperationalRecordRequests.UpsertDispatchEventRequest toRequest(UpsertDispatchEventResource resource) {
        return new OperationalRecordRequests.UpsertDispatchEventRequest(resource.tenantId(), resource.dispatchOrderId(),
            resource.status(), resource.description(), resource.visibleToBuyer());
    }

    public static ProofOfDeliveryRecordResource toResource(OperationalRecordResponses.ProofOfDeliveryRecordResponse response) {
        return new ProofOfDeliveryRecordResource(response.id(), response.tenantId(), response.dispatchOrderId(),
            response.receivedBy(), response.completedAt(), response.photoReference(), response.signatureReference(),
            response.notes(), response.status(), response.createdAt(), response.updatedAt());
    }

    public static OperationalRecordRequests.UpsertProofOfDeliveryRecordRequest toRequest(UpsertProofOfDeliveryRecordResource resource) {
        return new OperationalRecordRequests.UpsertProofOfDeliveryRecordRequest(resource.tenantId(), resource.dispatchOrderId(),
            resource.receivedBy(), resource.completedAt(), resource.photoReference(), resource.signatureReference(),
            resource.notes(), resource.status());
    }

    public static OperationalRecordRequests.CompletePodRequest toRequest(CompletePodResource resource) {
        return new OperationalRecordRequests.CompletePodRequest(resource.receivedBy(), resource.completedAt(),
            resource.photoReference(), resource.signatureReference(), resource.notes());
    }

    public static TemperatureLogResource toResource(OperationalRecordResponses.TemperatureLogResponse response) {
        return new TemperatureLogResource(response.id(), response.tenantId(), response.dispatchOrderId(), response.orderId(),
            response.celsius(), response.zone(), response.status(), response.recordedAt(), response.createdAt(),
            response.updatedAt());
    }

    public static OperationalRecordRequests.UpsertTemperatureLogRequest toRequest(UpsertTemperatureLogResource resource) {
        return new OperationalRecordRequests.UpsertTemperatureLogRequest(resource.tenantId(), resource.dispatchOrderId(),
            resource.orderId(), resource.celsius(), resource.zone(), resource.status(), resource.recordedAt());
    }
}
