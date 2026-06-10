package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.application.internal.LogisticsService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentsController {
    private final LogisticsService service;
    public ShipmentsController(LogisticsService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ShipmentResponse create(@Valid @RequestBody CreateShipmentRequest request) { return service.createShipment(request); }
    @GetMapping public List<ShipmentResponse> list() { return service.listShipments(); }
    @GetMapping("/{id}/tracking") public ShipmentResponse tracking(@PathVariable Long id) { return service.tracking(id); }
}
