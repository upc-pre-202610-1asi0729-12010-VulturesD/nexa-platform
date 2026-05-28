package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.dtos.*;
import com.nexa.platform.warehouse.application.internal.WarehouseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final WarehouseService service;
    public InventoryController(WarehouseService service) { this.service = service; }
    @GetMapping public List<InventoryResponse> list() { return service.listInventory(); }
    @GetMapping("/alerts") public List<InventoryResponse> alerts() { return service.listAlerts(); }
    @PostMapping("/movements") @ResponseStatus(HttpStatus.CREATED) public MovementResponse movement(@Valid @RequestBody MovementRequest request) { return service.registerMovement(request); }
}
