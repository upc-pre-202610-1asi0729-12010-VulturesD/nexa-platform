package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DriverChecklistResource;
import com.nexa.platform.logistics.interfaces.rest.transform.DriverChecklistResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/driver-checklists")
public class DriverChecklistsController {
    private final LogisticsService service;
    public DriverChecklistsController(LogisticsService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DriverChecklistResource create(@Valid @RequestBody DriverChecklistResource resource) {
        return DriverChecklistResourceAssembler.toResourceFromEntity(
            service.createChecklist(DriverChecklistResourceAssembler.toRequestFromResource(resource)));
    }
}
