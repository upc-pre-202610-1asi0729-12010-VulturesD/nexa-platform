package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.application.dtos.UpdateOrderRequest;
import com.nexa.platform.sales.interfaces.rest.resources.*;
import com.nexa.platform.sales.interfaces.rest.transform.CreateOrderCommandFromResourceAssembler;
import com.nexa.platform.sales.interfaces.rest.transform.OrderResourceFromEntityAssembler;
import com.nexa.platform.sales.interfaces.rest.transform.UpdateOrderStatusCommandFromResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@PreAuthorize("isAuthenticated()")
public class OrdersController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final SalesService service;
    private final CurrentWorkspaceContext workspace;
    public OrdersController(SalesService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource create(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @Valid @RequestBody CreateOrderResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.createOrder(workspace.requireTenant(tenantId),
                CreateOrderCommandFromResourceAssembler.toRequestFromResource(resource)));
    }
    @GetMapping public List<OrderResource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId) {
        return service.listOrders(workspace.requireTenant(tenantId), workspace.clientAccountId()).stream()
            .map(OrderResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public OrderResource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable String id) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.getOrder(workspace.requireTenant(tenantId), workspace.clientAccountId(), id));
    }

    @Operation(summary = "Get the cross-context timeline for an order")
    @GetMapping("/{id}/timeline")
    public OrderTimelineResource timeline(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        var response = service.getOrderTimeline(workspace.requireTenant(tenantId), workspace.clientAccountId(), id);
        return new OrderTimelineResource(response.orderId(), response.orderNumber(), response.events().stream()
            .map(event -> new OrderTimelineResource.TimelineEventResource(
                event.source(), event.status(), event.description(), event.occurredAt()))
            .toList());
    }

    @Operation(summary = "Update a pending order")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource update(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody UpdateOrderResource resource) {
        var request = new UpdateOrderRequest(resource.customerId(), resource.items().stream()
            .map(item -> new com.nexa.platform.sales.application.dtos.OrderItemRequest(item.productId(), item.quantity()))
            .toList(), resource.priority(), resource.notes());
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.updateOrder(workspace.requireTenant(tenantId), id, request));
    }

    @Operation(summary = "Confirm an order after payment and inventory reservation")
    @PostMapping("/{id}/confirmations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource confirm(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody ConfirmOrderResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.confirmOrder(workspace.requireTenant(tenantId), id,
                resource.paymentConfirmation(), resource.inventoryReservation()));
    }

    @Operation(summary = "Reject an order with a business reason")
    @PostMapping("/{id}/rejections")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource reject(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody RejectOrderResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.rejectOrder(workspace.requireTenant(tenantId), id, resource.rejectionReason()));
    }

    @Operation(summary = "Cancel an order before confirmation")
    @PostMapping("/{id}/cancellations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource cancel(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.cancelOrder(workspace.requireTenant(tenantId), id));
    }

    @Operation(summary = "Cancel an order using DELETE semantics")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId, @PathVariable Long id) {
        service.cancelOrder(workspace.requireTenant(tenantId), id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    public OrderResource status(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, @Valid @RequestBody UpdateOrderStatusResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.updateStatus(workspace.requireTenant(tenantId), id,
                UpdateOrderStatusCommandFromResourceAssembler.toRequestFromResource(resource)));
    }
}
