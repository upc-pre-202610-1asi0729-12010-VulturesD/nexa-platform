package com.nexa.platform.catalog.application.internal;

import com.nexa.platform.catalog.application.dtos.*;
import com.nexa.platform.catalog.domain.model.Category;
import com.nexa.platform.catalog.domain.model.ColdChainRequirement;
import com.nexa.platform.catalog.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {
    public ProductResponse toProductResponse(Product product) {
        ColdChainRequirement req = product.getColdChainRequirement();
        Integer minCelsius = req == null ? null : req.getMinCelsius();
        Integer maxCelsius = req == null ? null : req.getMaxCelsius();
        String handlingNotes = req == null ? null : req.getHandlingNotes();
        return new ProductResponse(product.getId(), product.getSku(), product.getSku(), product.getName(), product.getDescription(),
            product.getCategory().getName(), product.getSupplierName(), product.getSupplierName(), product.getUnitPrice(),
            product.getUnit(), minCelsius, maxCelsius, handlingNotes, product.getImageUrl(),
            product.isActive() ? "AVAILABLE" : "INACTIVE", product.isActive());
    }
    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
