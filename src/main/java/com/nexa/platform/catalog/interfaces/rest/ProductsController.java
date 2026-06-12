package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.commandservices.CatalogCommandService;
import com.nexa.platform.catalog.application.queries.GetProductByIdQuery;
import com.nexa.platform.catalog.application.queries.GetProductsQuery;
import com.nexa.platform.catalog.application.queryservices.CatalogQueryService;
import com.nexa.platform.catalog.interfaces.rest.resources.CreateProductResource;
import com.nexa.platform.catalog.interfaces.rest.resources.ProductResource;
import com.nexa.platform.catalog.interfaces.rest.resources.UpdateProductResource;
import com.nexa.platform.catalog.interfaces.rest.transform.CreateProductCommandFromResourceAssembler;
import com.nexa.platform.catalog.interfaces.rest.transform.ProductResourceFromEntityAssembler;
import com.nexa.platform.catalog.interfaces.rest.transform.UpdateProductCommandFromResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final CatalogCommandService commandService;
    private final CatalogQueryService queryService;
    public ProductsController(CatalogCommandService commandService, CatalogQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }
    @GetMapping public List<ProductResource> list() {
        return queryService.handle(new GetProductsQuery(true)).stream()
            .map(ProductResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }
    @GetMapping("/{id}") public ProductResource get(@PathVariable Long id) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(queryService.handle(new GetProductByIdQuery(id)));
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductResource create(@Valid @RequestBody CreateProductResource resource) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(commandService.handle(CreateProductCommandFromResourceAssembler.toCommandFromResource(resource)));
    }
    @PutMapping("/{id}") public ProductResource update(@PathVariable Long id, @Valid @RequestBody UpdateProductResource resource) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(commandService.handle(UpdateProductCommandFromResourceAssembler.toCommandFromResource(id, resource)));
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivate(@PathVariable Long id) { commandService.deactivateProduct(id); }
}
