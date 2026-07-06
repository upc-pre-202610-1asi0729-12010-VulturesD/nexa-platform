package com.nexa.platform.logistics.interfaces.rest.transform;

import com.nexa.platform.logistics.application.dtos.CreateShipmentRequest;
import com.nexa.platform.logistics.application.dtos.ShipmentResponse;
import com.nexa.platform.logistics.interfaces.rest.resources.ShipmentResource;

public final class ShipmentResourceAssembler {
    private ShipmentResourceAssembler() { }

    public static CreateShipmentRequest toRequestFromResource(ShipmentResource resource) {
        return new CreateShipmentRequest(resource.orderId(), resource.routeId(), resource.carrier(), resource.trackingNote());
    }

    public static ShipmentResource toResourceFromEntity(ShipmentResponse response) {
        return new ShipmentResource(response.id(), response.orderId(), null, response.route(),
            response.carrier(), response.status(), response.trackingNote());
    }
}
