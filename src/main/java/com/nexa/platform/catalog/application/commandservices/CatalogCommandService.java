package com.nexa.platform.catalog.application.commandservices;

import com.nexa.platform.catalog.application.commands.CreateProductCommand;
import com.nexa.platform.catalog.application.commands.UpdateProductCommand;
import com.nexa.platform.catalog.application.dtos.ProductResponse;

public interface CatalogCommandService {
    ProductResponse handle(CreateProductCommand command);
    ProductResponse handle(UpdateProductCommand command);
    void deactivateProduct(Long id);
}
