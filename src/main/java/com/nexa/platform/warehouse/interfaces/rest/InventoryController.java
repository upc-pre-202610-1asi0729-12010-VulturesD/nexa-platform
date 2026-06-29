package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryResource;
import com.nexa.platform.warehouse.interfaces.rest.resources.MovementResource;
import com.nexa.platform.warehouse.interfaces.rest.transform.InventoryResourceFromEntityAssembler;
import com.nexa.platform.warehouse.interfaces.rest.transform.MovementResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final WarehouseService service;
    private final CurrentWorkspaceContext workspace;
    public InventoryController(WarehouseService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }
    @GetMapping public List<InventoryResource> list(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listInventory(workspace.requireTenant(tenantId)).stream().map(InventoryResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/alerts") public List<InventoryResource> alerts(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listAlerts(workspace.requireTenant(tenantId)).stream().map(InventoryResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @PostMapping("/movements")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','LOGISTICS','WAREHOUSE','OPERATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResource movement(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody MovementResource resource) {
        return MovementResourceAssembler.toResourceFromEntity(
            service.registerMovement(workspace.requireTenant(tenantId), MovementResourceAssembler.toRequestFromResource(resource)));
    }
}
