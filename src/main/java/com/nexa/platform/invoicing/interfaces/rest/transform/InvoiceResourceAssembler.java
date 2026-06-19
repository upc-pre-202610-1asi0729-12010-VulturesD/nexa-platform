package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.CreateInvoiceRequest;
import com.nexa.platform.invoicing.application.dtos.InvoiceLineRequest;
import com.nexa.platform.invoicing.application.dtos.InvoiceResponse;
import com.nexa.platform.invoicing.interfaces.rest.resources.InvoiceResource;

public final class InvoiceResourceAssembler {
    private InvoiceResourceAssembler() { }

    public static CreateInvoiceRequest toRequestFromResource(InvoiceResource resource) {
        return new CreateInvoiceRequest(resource.orderId(), resource.lines().stream()
            .map(line -> new InvoiceLineRequest(line.description(), line.quantity(), line.unitPrice()))
            .toList());
    }

    public static InvoiceResource toResourceFromEntity(InvoiceResponse response) {
        return new InvoiceResource(response.id(), response.invoiceNumber(), response.orderId(),
            response.status(), response.total(), java.util.List.of());
    }
}
