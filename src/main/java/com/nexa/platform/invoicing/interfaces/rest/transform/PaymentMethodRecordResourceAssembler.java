package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.PaymentMethodRecordDtos;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentMethodRecordResources.*;

public final class PaymentMethodRecordResourceAssembler {
    private PaymentMethodRecordResourceAssembler() { }

    public static PaymentMethodRecordResource toResource(PaymentMethodRecordDtos.PaymentMethodRecordResponse response) {
        return new PaymentMethodRecordResource(response.id(), response.tenantId(), response.clientAccountId(),
            response.type(), response.label(), response.status(), response.isDefault(),
            response.createdAt(), response.updatedAt());
    }

    public static PaymentMethodRecordDtos.CreatePaymentMethodRecordRequest toRequest(
        CreatePaymentMethodRecordResource resource) {
        return new PaymentMethodRecordDtos.CreatePaymentMethodRecordRequest(
            resource.clientAccountId(), resource.type(), resource.label(), resource.isDefault());
    }

    public static PaymentMethodRecordDtos.ChangePaymentMethodRecordStatusRequest toRequest(
        ChangePaymentMethodRecordStatusResource resource) {
        return new PaymentMethodRecordDtos.ChangePaymentMethodRecordStatusRequest(
            resource.status(), resource.isDefault());
    }
}
