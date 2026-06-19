package com.nexa.platform.catalog.interfaces.rest.transform;

import com.nexa.platform.catalog.application.dtos.CategoryResponse;
import com.nexa.platform.catalog.interfaces.rest.resources.CategoryResource;

public final class CategoryResourceFromEntityAssembler {
    private CategoryResourceFromEntityAssembler() { }

    public static CategoryResource toResourceFromEntity(CategoryResponse response) {
        return new CategoryResource(response.id(), response.name(), response.description());
    }
}
