package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.PaymentProcessRecordDtos;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.ChangePaymentProcessStatusResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.CreatePaymentProcessRecordResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentProcessRecordResources.PaymentProcessRecordResource;

public final class PaymentProcessRecordResourceAssembler {
    private PaymentProcessRecordResourceAssembler() { }

    public static PaymentProcessRecordResource toResource(PaymentProcessRecordDtos.PaymentProcessRecordResponse response) {
        return new PaymentProcessRecordResource(response.id(), response.tenantId(), response.orderId(), response.clientAccountId(),
            response.paymentId(), response.paymentMethodRecordId(), response.subtotal(), response.discount(),
            response.shipping(), response.igv(), response.total(), response.status(), response.createdAt(), response.updatedAt());
    }

    public static PaymentProcessRecordDtos.CreatePaymentProcessRecordRequest toRequest(CreatePaymentProcessRecordResource resource) {
        return new PaymentProcessRecordDtos.CreatePaymentProcessRecordRequest(resource.tenantId(), resource.orderId(),
            resource.clientAccountId(), resource.paymentId(), resource.paymentMethodRecordId(), resource.subtotal(),
            resource.discount(), resource.shipping(), resource.igv(), resource.total(), resource.status());
    }

    public static PaymentProcessRecordDtos.ChangePaymentProcessStatusRequest toRequest(ChangePaymentProcessStatusResource resource) {
        return new PaymentProcessRecordDtos.ChangePaymentProcessStatusRequest(resource.status());
    }
}
