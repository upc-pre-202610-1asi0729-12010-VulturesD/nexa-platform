package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.PaymentRequest;
import com.nexa.platform.invoicing.application.dtos.PaymentResponse;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentResource;

public final class PaymentResourceAssembler {
    private PaymentResourceAssembler() { }

    public static PaymentRequest toRequestFromResource(PaymentResource resource) {
        return new PaymentRequest(resource.invoiceId(), resource.amount(), resource.method());
    }

    public static PaymentResource toResourceFromEntity(PaymentResponse response) {
        return new PaymentResource(response.id(), response.backendId(), response.invoiceId(), response.invoiceCode(),
            response.orderId(), response.referenceCode(), response.amount(), response.currency(), response.status(), response.method());
    }
}
