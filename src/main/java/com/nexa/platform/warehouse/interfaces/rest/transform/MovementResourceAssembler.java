package com.nexa.platform.warehouse.interfaces.rest.transform;

import com.nexa.platform.warehouse.application.dtos.MovementRequest;
import com.nexa.platform.warehouse.application.dtos.MovementResponse;
import com.nexa.platform.warehouse.interfaces.rest.resources.MovementResource;

public final class MovementResourceAssembler {
    private MovementResourceAssembler() { }

    public static MovementRequest toRequestFromResource(MovementResource resource) {
        return new MovementRequest(resource.inventoryItemId(), resource.type(), resource.quantityDelta(), resource.note());
    }

    public static MovementResource toResourceFromEntity(MovementResponse response) {
        return new MovementResource(response.id(), response.inventoryItemId(), response.type(),
            response.quantityDelta(), response.note(), response.quantityAvailable());
    }
}
