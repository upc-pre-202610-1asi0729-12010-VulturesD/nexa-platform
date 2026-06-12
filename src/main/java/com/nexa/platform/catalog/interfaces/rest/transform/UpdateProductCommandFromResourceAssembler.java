package com.nexa.platform.catalog.interfaces.rest.transform;

import com.nexa.platform.catalog.application.commands.UpdateProductCommand;
import com.nexa.platform.catalog.interfaces.rest.resources.UpdateProductResource;

public final class UpdateProductCommandFromResourceAssembler {
    private UpdateProductCommandFromResourceAssembler() { }

    public static UpdateProductCommand toCommandFromResource(Long productId, UpdateProductResource resource) {
        return new UpdateProductCommand(productId, resource.name(), resource.description(), resource.categoryId(),
            resource.supplierName(), resource.unitPrice(), resource.unit(), resource.imageUrl(), resource.minCelsius(),
            resource.maxCelsius(), resource.handlingNotes());
    }
}
