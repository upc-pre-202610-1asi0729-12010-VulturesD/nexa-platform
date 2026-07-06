package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.TemperatureLogResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertTemperatureLogResource;
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
@Tag(name = "Temperature Logs", description = "Cold-chain temperature monitoring records")
@PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
public class TemperatureLogsController {
    private final LogisticsService service;
    private final CurrentWorkspaceContext workspace;

    public TemperatureLogsController(LogisticsService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List temperature logs")
    @GetMapping("/api/v1/temperature-logs")
    public List<TemperatureLogResource> list() {
        Long tenantId = workspace.requireTenant(null);
        return service.listTemperatureLogs().stream().filter(row -> tenantId.equals(row.tenantId()))
            .map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "List temperature logs by dispatch order")
    @GetMapping("/api/v1/dispatch-orders/{dispatchOrderId}/temperature-logs")
    public List<TemperatureLogResource> listByDispatch(@PathVariable Long dispatchOrderId) {
        scopeDispatch(dispatchOrderId);
        return service.listTemperatureLogsByDispatch(dispatchOrderId).stream().map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get temperature log")
    @GetMapping("/api/v1/temperature-logs/{id}")
    public TemperatureLogResource get(@PathVariable Long id) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getTemperatureLog(id)));
    }

    @Operation(summary = "Create temperature log")
    @PostMapping("/api/v1/temperature-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public TemperatureLogResource create(@Valid @RequestBody UpsertTemperatureLogResource resource) {
        workspace.requireTenant(resource.tenantId());
        if (resource.dispatchOrderId() != null) scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(service.createTemperatureLog(DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Create temperature log for dispatch order")
    @PostMapping("/api/v1/dispatch-orders/{dispatchOrderId}/temperature-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public TemperatureLogResource createForDispatch(@PathVariable Long dispatchOrderId,
                                                    @Valid @RequestBody UpsertTemperatureLogResource resource) {
        scopeDispatch(dispatchOrderId);
        return DispatchOrderResourceAssembler.toResource(
            service.createTemperatureLogForDispatch(dispatchOrderId, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Update temperature log")
    @PutMapping("/api/v1/temperature-logs/{id}")
    public TemperatureLogResource update(@PathVariable Long id, @Valid @RequestBody UpsertTemperatureLogResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getTemperatureLog(id)));
        workspace.requireTenant(resource.tenantId());
        if (resource.dispatchOrderId() != null) scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(service.updateTemperatureLog(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Resolve temperature alert")
    @PostMapping("/api/v1/temperature-logs/{id}/resolve-alert")
    public TemperatureLogResource resolveAlert(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getTemperatureLog(id)));
        return DispatchOrderResourceAssembler.toResource(service.resolveTemperatureAlert(id));
    }

    @Operation(summary = "Delete temperature log")
    @DeleteMapping("/api/v1/temperature-logs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getTemperatureLog(id)));
        service.deleteTemperatureLog(id);
    }

    private TemperatureLogResource scoped(TemperatureLogResource resource) {
        workspace.requireTenant(resource.tenantId());
        return resource;
    }

    private void scopeDispatch(Long dispatchOrderId) {
        workspace.requireTenant(service.getDispatchOrder(dispatchOrderId).tenantId());
    }
}
