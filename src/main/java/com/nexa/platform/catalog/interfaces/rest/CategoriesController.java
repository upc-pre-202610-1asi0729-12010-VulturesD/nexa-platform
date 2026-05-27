package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.dtos.CategoryResponse;
import com.nexa.platform.catalog.application.internal.CatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {
    private final CatalogService service;
    public CategoriesController(CatalogService service) { this.service = service; }
    @GetMapping public List<CategoryResponse> list() { return service.listCategories(); }
}
