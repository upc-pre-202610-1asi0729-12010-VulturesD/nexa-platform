package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.ShipmentResource;
import com.nexa.platform.logistics.interfaces.rest.transform.ShipmentResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentsController {
    private final LogisticsService service;
    public ShipmentsController(LogisticsService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ShipmentResource create(@Valid @RequestBody ShipmentResource resource) {
        return ShipmentResourceAssembler.toResourceFromEntity(
            service.createShipment(ShipmentResourceAssembler.toRequestFromResource(resource)));
    }
    @GetMapping public List<ShipmentResource> list() {
        return service.listShipments().stream().map(ShipmentResourceAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}/tracking") public ShipmentResource tracking(@PathVariable Long id) {
        return ShipmentResourceAssembler.toResourceFromEntity(service.tracking(id));
    }
}
