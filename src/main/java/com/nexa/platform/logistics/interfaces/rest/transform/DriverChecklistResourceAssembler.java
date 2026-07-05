package com.nexa.platform.logistics.interfaces.rest.transform;

import com.nexa.platform.logistics.application.dtos.DriverChecklistRequest;
import com.nexa.platform.logistics.application.dtos.DriverChecklistResponse;
import com.nexa.platform.logistics.interfaces.rest.resources.DriverChecklistResource;

public final class DriverChecklistResourceAssembler {
    private DriverChecklistResourceAssembler() { }

    public static DriverChecklistRequest toRequestFromResource(DriverChecklistResource resource) {
        return new DriverChecklistRequest(resource.shipmentId(), resource.vehicleClean(),
            resource.temperatureRecorderReady(), resource.sealsVerified(), resource.notes());
    }

    public static DriverChecklistResource toResourceFromEntity(DriverChecklistResponse response) {
        return new DriverChecklistResource(response.id(), response.shipmentId(), response.vehicleClean(),
            response.temperatureRecorderReady(), response.sealsVerified(), response.notes());
    }
}
