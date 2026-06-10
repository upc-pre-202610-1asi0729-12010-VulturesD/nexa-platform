package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.dtos.*;
import com.nexa.platform.catalog.application.internal.CatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final CatalogService service;
    public ProductsController(CatalogService service) { this.service = service; }
    @GetMapping public List<ProductResponse> list() { return service.listProducts(); }
    @GetMapping("/{id}") public ProductResponse get(@PathVariable Long id) { return service.getProduct(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductResponse create(@Valid @RequestBody ProductRequest request) { return service.createProduct(request); }
    @PutMapping("/{id}") public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return service.updateProduct(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivate(@PathVariable Long id) { service.deactivateProduct(id); }
}
