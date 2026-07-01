package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.PurchaseRequestItemResponse;
import com.nexa.platform.sales.application.dtos.PurchaseRequestResponse;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestItemResource;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestResource;

public final class PurchaseRequestResourceFromEntityAssembler {
    private PurchaseRequestResourceFromEntityAssembler() { }

    public static PurchaseRequestResource toResourceFromEntity(PurchaseRequestResponse response) {
        return new PurchaseRequestResource(
            response.id(), response.id(), response.id(), response.tenantId(), response.clientAccountId(), response.code(), response.origin(),
            response.status(), response.priority(), response.requestedDeliveryDate(), response.deliveryAddress(),
            response.deliveryDistrict(), response.deliveryCity(), response.deliveryProvince(),
            response.deliveryReference(), response.paymentOption(), response.shippingEstimate(), response.comments(),
            response.commercialOwner(), response.createdAt(), response.updatedAt(), response.clientId(),
            response.deliveryAddressId(), response.documentProfile(),
            response.items().stream().map(PurchaseRequestResourceFromEntityAssembler::toItemResource).toList());
    }

    private static PurchaseRequestItemResource toItemResource(PurchaseRequestItemResponse response) {
        return new PurchaseRequestItemResource(response.productId(), response.name(), response.qty(), response.unit(), response.price());
    }
}
