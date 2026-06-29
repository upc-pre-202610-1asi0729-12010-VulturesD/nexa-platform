package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.catalog.application.dtos.BrandResponse;
import com.nexa.platform.catalog.application.dtos.UpsertReferenceRequest;
import com.nexa.platform.catalog.application.internal.CatalogService;
import com.nexa.platform.catalog.interfaces.rest.resources.BrandResource;
import com.nexa.platform.catalog.interfaces.rest.resources.UpsertReferenceResource;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brands")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Brands", description = "Catalog brand reference data")
public class BrandsController {
    private final CatalogService service;
    private final CurrentWorkspaceContext workspace;

    public BrandsController(CatalogService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public Object list(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) return toResource(service.getBrandByName(name));
        return service.listBrands().stream().map(BrandsController::toResource).toList();
    }

    @GetMapping("/{id}")
    public BrandResource get(@PathVariable Long id) {
        return toResource(service.getBrand(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BrandResource create(@Valid @RequestBody UpsertReferenceResource resource) {
        return toResource(service.createBrand(workspace.requireTenant(null), toRequest(resource)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BrandResource update(@PathVariable Long id, @Valid @RequestBody UpsertReferenceResource resource) {
        return toResource(service.updateBrand(workspace.requireTenant(null), id, toRequest(resource)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.deactivateBrand(workspace.requireTenant(null), id);
    }

    private static UpsertReferenceRequest toRequest(UpsertReferenceResource resource) {
        return new UpsertReferenceRequest(resource.name(), resource.description());
    }

    private static BrandResource toResource(BrandResponse response) {
        return new BrandResource(response.id(), response.name(), response.description(), response.isActive());
    }
}
