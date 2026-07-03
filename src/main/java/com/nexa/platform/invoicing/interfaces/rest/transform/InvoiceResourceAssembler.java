package com.nexa.platform.invoicing.interfaces.rest.transform;

import com.nexa.platform.invoicing.application.dtos.CreateInvoiceRequest;
import com.nexa.platform.invoicing.application.dtos.InvoiceLineRequest;
import com.nexa.platform.invoicing.application.dtos.InvoiceResponse;
import com.nexa.platform.invoicing.application.dtos.UpdateInvoiceRequest;
import com.nexa.platform.invoicing.interfaces.rest.resources.InvoiceResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.InvoiceLineResource;
import com.nexa.platform.invoicing.interfaces.rest.resources.UpdateInvoiceResource;

public final class InvoiceResourceAssembler {
    private InvoiceResourceAssembler() { }

    public static CreateInvoiceRequest toRequestFromResource(InvoiceResource resource) {
        return new CreateInvoiceRequest(resource.invoiceNumber(), resource.orderId(), resource.currency(), resource.lines().stream()
            .map(line -> new InvoiceLineRequest(line.description(), line.quantity(), line.unitPrice()))
            .toList());
    }

    public static UpdateInvoiceRequest toRequestFromResource(UpdateInvoiceResource resource) {
        return new UpdateInvoiceRequest(resource.invoiceNumber(), resource.orderId(), resource.currency(), resource.lines().stream()
            .map(line -> new InvoiceLineRequest(line.description(), line.quantity(), line.unitPrice()))
            .toList());
    }

    public static InvoiceResource toResourceFromEntity(InvoiceResponse response) {
        return new InvoiceResource(response.id(), response.tenantId(), response.invoiceNumber(), response.orderId(),
            response.currency(), response.status(), response.total(), response.paidAt(), response.lines().stream()
                .map(line -> new InvoiceLineResource(line.description(), line.quantity(), line.unitPrice()))
                .toList());
    }
}
