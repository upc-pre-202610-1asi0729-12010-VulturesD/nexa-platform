package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.DispatchEventResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertDispatchEventResource;
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
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@Tag(name = "Dispatch Events", description = "Dispatch timeline and buyer-visible tracking events")
@PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
public class DispatchEventsController {
    private final LogisticsService service;
    private final CurrentWorkspaceContext workspace;

    public DispatchEventsController(LogisticsService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List dispatch events")
    @GetMapping("/api/v1/dispatch-events")
    public List<DispatchEventResource> list() {
        Long tenantId = workspace.requireTenant(null);
        return service.listDispatchEvents().stream().filter(row -> tenantId.equals(row.tenantId()))
            .map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "List dispatch events by dispatch order")
    @GetMapping("/api/v1/dispatch-orders/{dispatchOrderId}/events")
    public List<DispatchEventResource> listByDispatch(@PathVariable Long dispatchOrderId) {
        scopeDispatch(dispatchOrderId);
        return service.listDispatchEventsByDispatch(dispatchOrderId).stream().map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get dispatch event")
    @GetMapping("/api/v1/dispatch-events/{id}")
    public DispatchEventResource get(@PathVariable Long id) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchEvent(id)));
    }

    @Operation(summary = "Create dispatch event")
    @PostMapping("/api/v1/dispatch-events")
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchEventResource create(@Valid @RequestBody UpsertDispatchEventResource resource) {
        workspace.requireTenant(resource.tenantId());
        scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(service.createDispatchEvent(DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Create dispatch event for dispatch order")
    @PostMapping("/api/v1/dispatch-orders/{dispatchOrderId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchEventResource createForDispatch(@PathVariable Long dispatchOrderId,
                                                   @RequestBody UpsertDispatchEventResource resource) {
        Long tenantId = scopeDispatch(dispatchOrderId);
        var request = new UpsertDispatchEventResource(tenantId, dispatchOrderId, resource.status(),
            resource.description(), resource.visibleToBuyer());
        return DispatchOrderResourceAssembler.toResource(service.createDispatchEvent(DispatchOrderResourceAssembler.toRequest(request)));
    }

    @Operation(summary = "Update dispatch event")
    @PutMapping("/api/v1/dispatch-events/{id}")
    public DispatchEventResource update(@PathVariable Long id, @Valid @RequestBody UpsertDispatchEventResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchEvent(id)));
        workspace.requireTenant(resource.tenantId());
        scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(service.updateDispatchEvent(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Delete dispatch event")
    @DeleteMapping("/api/v1/dispatch-events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchEvent(id)));
        service.deleteDispatchEvent(id);
    }

    private DispatchEventResource scoped(DispatchEventResource resource) {
        workspace.requireTenant(resource.tenantId());
        return resource;
    }

    private Long scopeDispatch(Long dispatchOrderId) {
        Long tenantId = service.getDispatchOrder(dispatchOrderId).tenantId();
        return workspace.requireTenant(tenantId);
    }
}
