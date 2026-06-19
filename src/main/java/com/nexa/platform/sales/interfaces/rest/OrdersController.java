package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.interfaces.rest.resources.CreateOrderResource;
import com.nexa.platform.sales.interfaces.rest.resources.OrderResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpdateOrderStatusResource;
import com.nexa.platform.sales.interfaces.rest.transform.CreateOrderCommandFromResourceAssembler;
import com.nexa.platform.sales.interfaces.rest.transform.OrderResourceFromEntityAssembler;
import com.nexa.platform.sales.interfaces.rest.transform.UpdateOrderStatusCommandFromResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final SalesService service;
    public OrdersController(SalesService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResource create(@Valid @RequestBody CreateOrderResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.createOrder(CreateOrderCommandFromResourceAssembler.toRequestFromResource(resource)));
    }
    @GetMapping public List<OrderResource> list() {
        return service.listOrders().stream().map(OrderResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public OrderResource get(@PathVariable String id) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(service.getOrder(id));
    }
    @PatchMapping("/{id}/status") public OrderResource status(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusResource resource) {
        return OrderResourceFromEntityAssembler.toResourceFromEntity(
            service.updateStatus(id, UpdateOrderStatusCommandFromResourceAssembler.toRequestFromResource(resource)));
    }
}
