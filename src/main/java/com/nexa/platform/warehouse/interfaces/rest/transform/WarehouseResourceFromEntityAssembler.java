package com.nexa.platform.warehouse.interfaces.rest.transform;

import com.nexa.platform.warehouse.application.dtos.WarehouseResponse;
import com.nexa.platform.warehouse.interfaces.rest.resources.WarehouseResource;

public final class WarehouseResourceFromEntityAssembler {
    private WarehouseResourceFromEntityAssembler() { }

    public static WarehouseResource toResourceFromEntity(WarehouseResponse response) {
        return new WarehouseResource(response.id(), response.name(), response.address(), response.temperatureBand());
    }
}
