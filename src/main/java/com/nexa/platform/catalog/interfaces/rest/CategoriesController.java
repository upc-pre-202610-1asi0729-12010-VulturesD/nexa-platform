package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.dtos.CategoryResponse;
import com.nexa.platform.catalog.application.dtos.UpsertReferenceRequest;
import com.nexa.platform.catalog.application.internal.CatalogService;
import com.nexa.platform.catalog.interfaces.rest.resources.CategoryResource;
import com.nexa.platform.catalog.interfaces.rest.resources.UpsertReferenceResource;
import com.nexa.platform.catalog.interfaces.rest.transform.CategoryResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Categories", description = "Catalog category reference data")
public class CategoriesController {
    private final CatalogService service;
    private final CurrentWorkspaceContext workspace;

    public CategoriesController(CatalogService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public Object list(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) return toResource(service.getCategoryByName(name));
        return service.listCategories().stream()
            .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public CategoryResource get(@PathVariable Long id) {
        return toResource(service.getCategory(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResource create(@Valid @RequestBody UpsertReferenceResource resource) {
        return toResource(service.createCategory(workspace.requireTenant(null), toRequest(resource)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResource update(@PathVariable Long id, @Valid @RequestBody UpsertReferenceResource resource) {
        return toResource(service.updateCategory(workspace.requireTenant(null), id, toRequest(resource)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.deactivateCategory(workspace.requireTenant(null), id);
    }

    private static UpsertReferenceRequest toRequest(UpsertReferenceResource resource) {
        return new UpsertReferenceRequest(resource.name(), resource.description());
    }

    private static CategoryResource toResource(CategoryResponse response) {
        return CategoryResourceFromEntityAssembler.toResourceFromEntity(response);
    }
}
