package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryResource;
import com.nexa.platform.warehouse.interfaces.rest.resources.MovementResource;
import com.nexa.platform.warehouse.interfaces.rest.transform.InventoryResourceFromEntityAssembler;
import com.nexa.platform.warehouse.interfaces.rest.transform.MovementResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final WarehouseService service;
    public InventoryController(WarehouseService service) { this.service = service; }
    @GetMapping public List<InventoryResource> list() {
        return service.listInventory().stream().map(InventoryResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/alerts") public List<InventoryResource> alerts() {
        return service.listAlerts().stream().map(InventoryResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @PostMapping("/movements") @ResponseStatus(HttpStatus.CREATED) public MovementResource movement(@Valid @RequestBody MovementResource resource) {
        return MovementResourceAssembler.toResourceFromEntity(
            service.registerMovement(MovementResourceAssembler.toRequestFromResource(resource)));
    }
}
