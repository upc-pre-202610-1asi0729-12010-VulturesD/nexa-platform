package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.BusinessDocumentResponse;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentResource;

public final class BusinessDocumentResourceFromEntityAssembler {
    private BusinessDocumentResourceFromEntityAssembler() { }

    public static BusinessDocumentResource toResourceFromEntity(BusinessDocumentResponse response) {
        return new BusinessDocumentResource(response.id(), response.clientId(), response.orderId(), response.type(),
            response.label(), response.fileName(), response.status(), response.visibleToBuyer(), response.required(),
            response.dueDate(), response.amount());
    }
}
