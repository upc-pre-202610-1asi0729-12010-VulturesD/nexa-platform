package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.dtos.UpsertDispatchOrderRequest;
import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.CreateDispatchOrderResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.DispatchOrderResource;
import com.nexa.platform.logistics.interfaces.rest.transform.DispatchOrderResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@Tag(name = "Dispatch Orders", description = "Order dispatch aliases")
@PreAuthorize("isAuthenticated()")
public class OrderDispatchOrdersController {
    private final LogisticsService service;
    private final CurrentWorkspaceContext workspace;

    public OrderDispatchOrdersController(LogisticsService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "Create dispatch order for order")
    @PostMapping("/api/v1/orders/{orderId}/dispatch-orders")
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchOrderResource createForOrder(@PathVariable Long orderId, @RequestBody CreateDispatchOrderResource resource) {
        Long tenantId = workspace.requireTenant(null);
        String code = resource.code() == null || resource.code().isBlank() ? "DSP-ORD-" + orderId : resource.code();
        String routeName = resource.routeName() == null || resource.routeName().isBlank() ? "Default cold route" : resource.routeName();
        var request = new UpsertDispatchOrderRequest(tenantId, orderId, resource.clientAccountId(), code, "ready_for_operations", routeName, "", null, "");
        return scoped(DispatchOrderResourceAssembler.toResource(service.createDispatchOrder(request)));
    }

    @Operation(summary = "Get order tracking dispatch")
    @GetMapping("/api/v1/orders/{orderId}/tracking")
    public DispatchOrderResource tracking(@PathVariable Long orderId) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getDispatchOrderForOrder(orderId)));
    }

    private DispatchOrderResource scoped(DispatchOrderResource resource) {
        workspace.requireTenant(resource.tenantId());
        return resource;
    }
}
