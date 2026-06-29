package com.nexa.platform.warehouse.interfaces.rest;

import com.nexa.platform.warehouse.application.internal.WarehouseService;
import com.nexa.platform.warehouse.interfaces.rest.resources.InventoryOperationResources.*;
import com.nexa.platform.warehouse.interfaces.rest.transform.InventoryOperationResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
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
@RequestMapping(value = "/api/v1/reservations", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Inventory Reservations", description = "Tenant-scoped stock reservation and release lifecycle")
@PreAuthorize("isAuthenticated()")
public class ReservationsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final WarehouseService service;
    private final CurrentWorkspaceContext workspace;

    public ReservationsController(WarehouseService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List inventory reservations")
    @GetMapping
    public List<InventoryReservationResource> list(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listReservations(workspace.requireTenant(tenantId)).stream()
            .map(InventoryOperationResourceAssembler::toResource)
            .toList();
    }

    @Operation(summary = "Get inventory reservation")
    @GetMapping("/{id}")
    public InventoryReservationResource get(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return InventoryOperationResourceAssembler.toResource(service.getReservation(workspace.requireTenant(tenantId), id));
    }

    @Operation(summary = "Reserve inventory")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE','OPERATOR')")
    public ResponseEntity<InventoryReservationResource> create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody CreateInventoryReservationResource resource) {
        var created = InventoryOperationResourceAssembler.toResource(
            service.createReservation(workspace.requireTenant(tenantId), InventoryOperationResourceAssembler.toRequest(resource)));
        return ResponseEntity.created(URI.create("/api/v1/reservations/" + created.id())).body(created);
    }

    @Operation(summary = "Release inventory reservation")
    @PostMapping("/{id}/releases")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE','OPERATOR')")
    public InventoryReservationResource release(@RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
                                                @PathVariable Long id) {
        return InventoryOperationResourceAssembler.toResource(service.releaseReservation(workspace.requireTenant(tenantId), id));
    }
}
