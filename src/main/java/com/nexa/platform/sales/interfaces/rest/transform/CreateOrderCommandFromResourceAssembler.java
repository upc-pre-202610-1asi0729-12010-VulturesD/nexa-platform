package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.CreateOrderRequest;
import com.nexa.platform.sales.application.dtos.OrderItemRequest;
import com.nexa.platform.sales.interfaces.rest.resources.CreateOrderResource;

public final class CreateOrderCommandFromResourceAssembler {
    private CreateOrderCommandFromResourceAssembler() { }

    public static CreateOrderRequest toRequestFromResource(CreateOrderResource resource) {
        return new CreateOrderRequest(resource.customerId(), resource.items().stream()
            .map(item -> new OrderItemRequest(item.productId(), item.quantity()))
            .toList());
    }
}
