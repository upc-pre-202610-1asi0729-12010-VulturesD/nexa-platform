package com.nexa.platform.catalog.application.queryservices;

import com.nexa.platform.catalog.application.dtos.CategoryResponse;
import com.nexa.platform.catalog.application.dtos.ProductResponse;
import com.nexa.platform.catalog.application.queries.GetCatalogCategoriesQuery;
import com.nexa.platform.catalog.application.queries.GetProductByIdQuery;
import com.nexa.platform.catalog.application.queries.GetProductsQuery;
import java.util.List;

public interface CatalogQueryService {
    List<ProductResponse> handle(GetProductsQuery query);
    ProductResponse handle(GetProductByIdQuery query);
    List<CategoryResponse> handle(GetCatalogCategoriesQuery query);
}
