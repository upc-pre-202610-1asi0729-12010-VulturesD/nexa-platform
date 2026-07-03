package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.BusinessDocumentDtos;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentA1Resources.*;

public final class BusinessDocumentA1ResourceAssembler {
    private BusinessDocumentA1ResourceAssembler() { }

    public static BusinessDocumentA1Resource toResource(BusinessDocumentDtos.BusinessDocumentA1Response response) {
        return new BusinessDocumentA1Resource(response.id(), response.tenantId(), response.orderId(),
            response.clientAccountId(), response.documentTypeId(), response.type(), response.label(), response.status(),
            response.fileName(), response.visibleToBuyer(), response.required(), response.createdAt(), response.updatedAt());
    }

    public static BusinessDocumentDtos.CreateBusinessDocumentRequest toRequest(CreateBusinessDocumentResource resource) {
        return new BusinessDocumentDtos.CreateBusinessDocumentRequest(resource.tenantId(), resource.orderId(),
            resource.clientAccountId(), resource.documentTypeId(), resource.type(), resource.label(),
            resource.visibleToBuyer(), resource.required(), resource.fileName());
    }

    public static BusinessDocumentDtos.GenerateBusinessDocumentRequest toRequest(GenerateBusinessDocumentResource resource) {
        return new BusinessDocumentDtos.GenerateBusinessDocumentRequest(resource.tenantId(), resource.orderId(), resource.type());
    }

    public static BusinessDocumentDtos.ChangeBusinessDocumentStatusRequest toRequest(ChangeBusinessDocumentStatusResource resource) {
        return new BusinessDocumentDtos.ChangeBusinessDocumentStatusRequest(resource.status(), resource.visibleToBuyer());
    }
}
