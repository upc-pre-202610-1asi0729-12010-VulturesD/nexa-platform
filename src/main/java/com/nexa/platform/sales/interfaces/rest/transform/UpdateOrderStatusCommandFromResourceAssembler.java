package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.UpdateOrderStatusRequest;
import com.nexa.platform.sales.interfaces.rest.resources.UpdateOrderStatusResource;

public final class UpdateOrderStatusCommandFromResourceAssembler {
    private UpdateOrderStatusCommandFromResourceAssembler() { }

    public static UpdateOrderStatusRequest toRequestFromResource(UpdateOrderStatusResource resource) {
        return new UpdateOrderStatusRequest(resource.status());
    }
}
