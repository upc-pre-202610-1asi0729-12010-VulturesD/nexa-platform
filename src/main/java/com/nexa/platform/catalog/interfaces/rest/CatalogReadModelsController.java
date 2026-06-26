package com.nexa.platform.catalog.interfaces.rest;

import com.nexa.platform.shared.application.pagination.PagedResult;
import com.nexa.platform.shared.application.readmodels.PromotionalCatalogItemReadModel;
import com.nexa.platform.shared.application.readmodels.WorkspaceReadModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catalog read models", description = "Cross-context catalog projections")
public class CatalogReadModelsController {
    private final WorkspaceReadModelService readModels;

    public CatalogReadModelsController(WorkspaceReadModelService readModels) {
        this.readModels = readModels;
    }

    @GetMapping("/promotional-catalog")
    public PagedResult<PromotionalCatalogItemReadModel> promotionalCatalog(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize) {
        return readModels.promotionalCatalog(page, pageSize);
    }
}
