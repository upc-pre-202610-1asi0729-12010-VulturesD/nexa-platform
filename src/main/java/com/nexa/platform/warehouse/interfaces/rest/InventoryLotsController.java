package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryOperationResources.*;
import com.nexa.platform.warehouse.interfaces.rest.transform.InventoryOperationResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/inventory-lots", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Inventory Lots", description = "Tenant-scoped FEFO lot traceability")
@PreAuthorize("isAuthenticated()")
public class InventoryLotsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final WarehouseService service;
    private final CurrentWorkspaceContext workspace;

    public InventoryLotsController(WarehouseService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List inventory lots")
    @GetMapping
    public List<InventoryLotResource> list(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listLots(workspace.requireTenant(tenantId)).stream().map(InventoryOperationResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get inventory lot by lot code")
    @GetMapping("/{lotCode}")
    public InventoryLotResource get(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String lotCode) {
        return InventoryOperationResourceAssembler.toResource(service.getLot(workspace.requireTenant(tenantId), lotCode));
    }

    @Operation(summary = "Create inventory lot")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE','OPERATOR')")
    public ResponseEntity<InventoryLotResource> create(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
                                                       @Valid @RequestBody UpsertInventoryLotResource resource) {
        var created = InventoryOperationResourceAssembler.toResource(
            service.createLot(workspace.requireTenant(tenantId), InventoryOperationResourceAssembler.toRequest(resource)));
        return ResponseEntity.created(URI.create("/api/v1/inventory-lots/" + created.lotCode())).body(created);
    }

    @Operation(summary = "Update inventory lot")
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE','OPERATOR')")
    public InventoryLotResource update(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id,
                                       @Valid @RequestBody UpsertInventoryLotResource resource) {
        return InventoryOperationResourceAssembler.toResource(
            service.updateLot(workspace.requireTenant(tenantId), id, InventoryOperationResourceAssembler.toRequest(resource)));
    }
}
