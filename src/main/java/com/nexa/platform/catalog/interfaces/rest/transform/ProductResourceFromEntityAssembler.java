package com.nexa.platform.catalog.interfaces.rest.transform;

import com.nexa.platform.catalog.application.dtos.ProductResponse;
import com.nexa.platform.catalog.interfaces.rest.resources.ProductResource;

public final class ProductResourceFromEntityAssembler {
    private ProductResourceFromEntityAssembler() { }

    public static ProductResource toResourceFromEntity(ProductResponse response) {
        return new ProductResource(response.id(), response.sku(), response.productCode(), response.name(),
            response.description(), response.category(), response.supplierName(), response.brandName(), response.unitPrice(),
            response.unit(), response.minCelsius(), response.maxCelsius(), response.handlingNotes(), response.imageUrl(),
            response.status(), response.active(), response.brandName(), response.unitPrice(), response.status(), response.availableStock(),
            temperatureRange(response), response.productCode(), response.productCode(), response.name(), response.category(),
            response.unitPrice(), "PEN", response.availableStock(), response.reservedStock(), response.minStock(),
            response.handlingNotes(), response.active());
    }

    private static String temperatureRange(ProductResponse response) {
        if (response.minCelsius() == null || response.maxCelsius() == null) return null;
        return response.minCelsius() + "C - " + response.maxCelsius() + "C";
    }
}
