package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.PurchaseRequestLineService;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestLineResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertPurchaseRequestLineResource;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchase-request-lines")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Purchase request lines", description = "Editable buyer request line items")
public class PurchaseRequestLinesController {
    private final PurchaseRequestLineService service;
    private final CurrentWorkspaceContext workspace;

    public PurchaseRequestLinesController(PurchaseRequestLineService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @GetMapping
    public List<PurchaseRequestLineResource> list() {
        return service.list(workspace.requireTenant(null), workspace.clientAccountId());
    }

    @GetMapping("/{id}")
    public PurchaseRequestLineResource get(@PathVariable Long id) {
        return service.get(workspace.requireTenant(null), workspace.clientAccountId(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequestLineResource create(@Valid @RequestBody UpsertPurchaseRequestLineResource resource) {
        return service.create(workspace.requireTenant(resource.tenantId()), workspace.clientAccountId(), resource);
    }

    @PutMapping("/{id}")
    public PurchaseRequestLineResource update(@PathVariable Long id,
                                              @Valid @RequestBody UpsertPurchaseRequestLineResource resource) {
        return service.update(workspace.requireTenant(resource.tenantId()), workspace.clientAccountId(), id, resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(workspace.requireTenant(null), workspace.clientAccountId(), id);
    }
}
