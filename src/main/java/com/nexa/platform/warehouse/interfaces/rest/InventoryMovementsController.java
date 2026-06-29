package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryOperationResources.*;
import com.nexa.platform.warehouse.interfaces.rest.transform.InventoryOperationResourceAssembler;
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
@RequestMapping(value = {"/api/v1/inventory-movements", "/api/v1/stock-movements"}, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Inventory Movements", description = "Tenant-scoped inventory movement ledger")
@PreAuthorize("isAuthenticated()")
public class InventoryMovementsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final WarehouseService service;
    private final CurrentWorkspaceContext workspace;

    public InventoryMovementsController(WarehouseService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List inventory movements")
    @GetMapping
    public List<InventoryMovementResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listMovements(workspace.requireTenant(tenantId)).stream()
            .map(InventoryOperationResourceAssembler::toResource)
            .toList();
    }

    @Operation(summary = "Get inventory movement by code")
    @GetMapping("/{code}")
    public InventoryMovementResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String code) {
        return InventoryOperationResourceAssembler.toResource(
            service.getMovement(workspace.requireTenant(tenantId), code));
    }

    @Operation(summary = "Create inventory movement and apply stock change")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS','WAREHOUSE','OPERATOR')")
    public ResponseEntity<InventoryMovementResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody CreateInventoryMovementResource resource) {
        var created = InventoryOperationResourceAssembler.toResource(service.createMovement(
            workspace.requireTenant(tenantId), InventoryOperationResourceAssembler.toRequest(resource)));
        return ResponseEntity.created(URI.create("/api/v1/inventory-movements/" + created.code())).body(created);
    }
}
