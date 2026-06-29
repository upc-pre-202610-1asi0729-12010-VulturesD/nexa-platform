package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.WarehouseResource;
import com.nexa.platform.warehouse.interfaces.rest.transform.WarehouseResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehousesController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final WarehouseService service;
    private final CurrentWorkspaceContext workspace;
    public WarehousesController(WarehouseService service, CurrentWorkspaceContext workspace) { this.service = service; this.workspace = workspace; }
    @GetMapping public List<WarehouseResource> list(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listWarehouses(workspace.requireTenant(tenantId)).stream().map(WarehouseResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}
