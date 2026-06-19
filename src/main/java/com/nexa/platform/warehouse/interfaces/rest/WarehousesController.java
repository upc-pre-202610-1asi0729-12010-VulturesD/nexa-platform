package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.WarehouseResource;
import com.nexa.platform.warehouse.interfaces.rest.transform.WarehouseResourceFromEntityAssembler;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehousesController {
    private final WarehouseService service;
    public WarehousesController(WarehouseService service) { this.service = service; }
    @GetMapping public List<WarehouseResource> list() {
        return service.listWarehouses().stream().map(WarehouseResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}
