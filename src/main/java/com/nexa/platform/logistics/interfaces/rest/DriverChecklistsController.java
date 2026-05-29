package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.dtos.*;
import com.nexa.platform.logistics.application.internal.LogisticsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver-checklists")
public class DriverChecklistsController {
    private final LogisticsService service;
    public DriverChecklistsController(LogisticsService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DriverChecklistResponse create(@Valid @RequestBody DriverChecklistRequest request) { return service.createChecklist(request); }
}
