package com.nexa.platform.catalog.interfaces.rest.transform;

import com.nexa.platform.catalog.application.dtos.ProductResponse;
import com.nexa.platform.catalog.interfaces.rest.resources.ProductResource;

public final class ProductResourceFromEntityAssembler {
    private ProductResourceFromEntityAssembler() { }

    public static ProductResource toResourceFromEntity(ProductResponse response) {
        return new ProductResource(response.id(), response.sku(), response.productCode(), response.name(),
            response.description(), response.category(), response.supplierName(), response.brandName(), response.unitPrice(),
            response.unit(), response.minCelsius(), response.maxCelsius(), response.handlingNotes(), response.imageUrl(),
            response.status(), response.active());
    }
}
