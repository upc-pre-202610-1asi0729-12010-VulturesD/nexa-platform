package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.queries.GetCatalogCategoriesQuery;
import com.nexa.platform.catalog.application.queryservices.CatalogQueryService;
import com.nexa.platform.catalog.interfaces.rest.resources.CategoryResource;
import com.nexa.platform.catalog.interfaces.rest.transform.CategoryResourceFromEntityAssembler;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {
    private final CatalogQueryService queryService;
    public CategoriesController(CatalogQueryService queryService) { this.queryService = queryService; }
    @GetMapping public List<CategoryResource> list() {
        return queryService.handle(new GetCatalogCategoriesQuery()).stream()
            .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }
}
