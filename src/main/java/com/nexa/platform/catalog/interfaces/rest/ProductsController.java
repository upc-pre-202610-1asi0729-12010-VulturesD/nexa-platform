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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for catalog product operations.
 *
 * <p>Delegates command operations to {@link CatalogCommandService} and
 * query operations to {@link CatalogQueryService}, following the CQRS pattern
 * from the course reference implementation.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/api/v1/products", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Products", description = "Catalog product management endpoints")
public class ProductsController {

    private final CatalogCommandService commandService;
    private final CatalogQueryService queryService;

    public ProductsController(CatalogCommandService commandService, CatalogQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Operation(summary = "List all active products", description = "Returns all active products in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product list returned")
    })
    @GetMapping
    public List<ProductResource> list() {
        return queryService.handle(new GetProductsQuery(true)).stream()
            .map(ProductResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @Operation(summary = "Get product by ID", description = "Returns a single product by its identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductResource get(@PathVariable Long id) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(queryService.handle(new GetProductByIdQuery(id)));
    }

    @Operation(summary = "Create a product", description = "Creates a new product in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "409", description = "SKU already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource create(@Valid @RequestBody CreateProductResource resource) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(
            commandService.handle(CreateProductCommandFromResourceAssembler.toCommandFromResource(resource)));
    }

    @Operation(summary = "Update a product", description = "Updates an existing product's details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ProductResource update(@PathVariable Long id, @Valid @RequestBody UpdateProductResource resource) {
        return ProductResourceFromEntityAssembler.toResourceFromEntity(
            commandService.handle(UpdateProductCommandFromResourceAssembler.toCommandFromResource(id, resource)));
    }

    @Operation(summary = "Deactivate a product", description = "Soft-deletes a product by marking it as inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deactivated"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        commandService.deactivateProduct(id);
    }
}
