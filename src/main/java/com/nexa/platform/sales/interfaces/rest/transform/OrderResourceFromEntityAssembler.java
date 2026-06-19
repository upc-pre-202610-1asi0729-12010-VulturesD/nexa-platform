package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.OrderItemResponse;
import com.nexa.platform.sales.application.dtos.OrderResponse;
import com.nexa.platform.sales.interfaces.rest.resources.OrderItemResource;
import com.nexa.platform.sales.interfaces.rest.resources.OrderResource;

public final class OrderResourceFromEntityAssembler {
    private OrderResourceFromEntityAssembler() { }

    public static OrderResource toResourceFromEntity(OrderResponse response) {
        return new OrderResource(response.id(), response.backendId(), response.orderNumber(), response.customerId(),
            response.clientId(), response.customer(), response.status(), response.priority(), response.date(),
            response.deliveryDate(), response.items().stream().map(OrderResourceFromEntityAssembler::toItemResource).toList(),
            response.total(), response.notes());
    }

    private static OrderItemResource toItemResource(OrderItemResponse response) {
        return new OrderItemResource(response.productId(), response.sku(), response.name(), response.quantity(),
            response.unitPrice(), response.subtotal());
    }
}
