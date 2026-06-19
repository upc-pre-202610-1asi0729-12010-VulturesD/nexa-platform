package com.nexa.platform.warehouse.interfaces.rest.transform;

import com.nexa.platform.warehouse.application.dtos.InventoryResponse;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryResource;

public final class InventoryResourceFromEntityAssembler {
    private InventoryResourceFromEntityAssembler() { }

    public static InventoryResource toResourceFromEntity(InventoryResponse response) {
        return new InventoryResource(response.id(), response.warehouse(), response.productSku(), response.productName(),
            response.quantityAvailable(), response.reorderPoint(), response.lowStock());
    }
}
