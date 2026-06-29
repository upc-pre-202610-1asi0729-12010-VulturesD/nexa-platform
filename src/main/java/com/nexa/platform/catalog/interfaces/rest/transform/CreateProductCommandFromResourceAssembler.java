package com.nexa.platform.catalog.interfaces.rest.transform;

import com.nexa.platform.catalog.application.commands.CreateProductCommand;
import com.nexa.platform.catalog.interfaces.rest.resources.CreateProductResource;

public final class CreateProductCommandFromResourceAssembler {
    private CreateProductCommandFromResourceAssembler() { }

    public static CreateProductCommand toCommandFromResource(CreateProductResource resource) {
        return new CreateProductCommand(resource.sku(), resource.name(), resource.description(), resource.categoryId(),
            resource.supplierName(), resource.unitPrice(), resource.unit(), resource.imageUrl(), resource.minCelsius(),
            resource.maxCelsius(), resource.handlingNotes());
    }
}
