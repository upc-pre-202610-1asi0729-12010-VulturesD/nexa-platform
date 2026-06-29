package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.queries.GetProductByIdQuery;
import com.nexa.platform.catalog.application.queries.GetProductsQuery;
import com.nexa.platform.catalog.application.queryservices.CatalogQueryService;
import com.nexa.platform.catalog.interfaces.rest.resources.ProductResource;
import com.nexa.platform.catalog.interfaces.rest.transform.ProductResourceFromEntityAssembler;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog-items")
public class CatalogItemsController {
    private final CatalogQueryService queryService;

    public CatalogItemsController(CatalogQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public List<ProductResource> list() {
        return queryService.handle(new GetProductsQuery(true)).stream()
            .map(ProductResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public ProductResource get(@PathVariable Long id) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(queryService.handle(new GetProductByIdQuery(id)));
    }
}
