package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.PurchaseRequestItemResponse;
import com.nexa.platform.sales.application.dtos.PurchaseRequestResponse;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestItemResource;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestResource;

public final class PurchaseRequestResourceFromEntityAssembler {
    private PurchaseRequestResourceFromEntityAssembler() { }

    public static PurchaseRequestResource toResourceFromEntity(PurchaseRequestResponse response) {
        return new PurchaseRequestResource(response.id(), response.clientId(), response.status(), response.priority(),
            response.requestedDeliveryDate(), response.deliveryAddressId(), response.documentProfile(), response.comments(),
            response.createdAt(), response.items().stream().map(PurchaseRequestResourceFromEntityAssembler::toItemResource).toList());
    }

    private static PurchaseRequestItemResource toItemResource(PurchaseRequestItemResponse response) {
        return new PurchaseRequestItemResource(response.productId(), response.name(), response.qty(), response.unit(), response.price());
    }
}
