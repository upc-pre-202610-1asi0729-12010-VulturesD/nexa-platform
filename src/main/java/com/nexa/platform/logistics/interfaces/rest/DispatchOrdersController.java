package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.dtos.UpsertDispatchOrderRequest;
import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.*;
import com.nexa.platform.logistics.interfaces.rest.transform.DispatchOrderResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/dispatch-orders", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Dispatch Orders", description = "Dispatch order lifecycle endpoints")
@PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
public class DispatchOrdersController {
    private final LogisticsService service;
    private final CurrentWorkspaceContext workspace;

    public DispatchOrdersController(LogisticsService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List dispatch orders")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS','SALES')")
    public List<DispatchOrderResource> list() {
        Long tenantId = workspace.requireTenant(null);
        return service.listDispatchOrders().stream().filter(row -> tenantId.equals(row.tenantId()))
            .map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get dispatch order by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS','SALES')")
    public DispatchOrderResource get(@PathVariable Long id) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
    }

    @Operation(summary = "Create dispatch order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchOrderResource create(@Valid @RequestBody UpsertDispatchOrderResource resource) {
        workspace.requireTenant(resource.tenantId());
        return DispatchOrderResourceAssembler.toResource(
            service.createDispatchOrder(DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Update dispatch order")
    @PutMapping("/{id}")
    public DispatchOrderResource update(@PathVariable Long id, @Valid @RequestBody UpsertDispatchOrderResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        workspace.requireTenant(resource.tenantId());
        return DispatchOrderResourceAssembler.toResource(
            service.updateDispatchOrder(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Patch dispatch order")
    @PatchMapping("/{id}")
    public DispatchOrderResource patch(@PathVariable Long id, @RequestBody UpsertDispatchOrderResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        workspace.requireTenant(resource.tenantId());
        return DispatchOrderResourceAssembler.toResource(
            service.updateDispatchOrder(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Delete dispatch order")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        service.deleteDispatchOrder(id);
    }

    @Operation(summary = "Assign dispatch order")
    @PostMapping("/{id}/assignees")
    public DispatchOrderResource assign(@PathVariable Long id, @Valid @RequestBody AssignDispatchResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.assignDispatchOrder(id, resource.responsible()));
    }

    @Operation(summary = "Schedule dispatch order")
    @PostMapping("/{id}/schedules")
    public DispatchOrderResource schedule(@PathVariable Long id, @Valid @RequestBody ScheduleDispatchResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.scheduleDispatchOrder(id, resource.eta(), resource.deliveryWindow(), resource.note()));
    }

    @Operation(summary = "Start dispatch route")
    @PostMapping("/{id}/route-starts")
    public DispatchOrderResource startRoute(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.startDispatchRoute(id));
    }

    @Operation(summary = "Complete dispatch delivery")
    @PostMapping("/{id}/deliveries")
    public DispatchOrderResource complete(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.completeDispatchDelivery(id));
    }

    @Operation(summary = "Mark dispatch incident")
    @PostMapping("/{id}/incidents")
    public DispatchOrderResource incident(@PathVariable Long id, @RequestBody(required = false) DispatchNoteResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.incidentDispatchOrder(id, resource == null ? null : resource.note()));
    }

    @Operation(summary = "Reschedule dispatch order")
    @PostMapping("/{id}/reschedules")
    public DispatchOrderResource reschedule(@PathVariable Long id, @Valid @RequestBody ScheduleDispatchResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.rescheduleDispatchOrder(id, resource.eta(), resource.deliveryWindow(), resource.note()));
    }

    @Operation(summary = "Change dispatch status")
    @PostMapping("/{id}/status-changes")
    public DispatchOrderResource changeStatus(@PathVariable Long id, @Valid @RequestBody DispatchStatusChangeResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrder(id)));
        return DispatchOrderResourceAssembler.toResource(service.changeDispatchStatus(
            id, resource.status(), resource.note(), resource.visibleToBuyer()));
    }

    @Operation(summary = "Get order dispatch tracking")
    @GetMapping("/by-order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS','SALES')")
    public DispatchOrderResource byOrder(@PathVariable Long orderId) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrderForOrder(orderId)));
    }

    private DispatchOrderResource scoped(DispatchOrderResource resource) {
        workspace.requireTenant(resource.tenantId());
        return resource;
    }
}
