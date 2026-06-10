package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.application.internal.SalesService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final SalesService service;
    public OrdersController(SalesService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) { return service.createOrder(request); }
    @GetMapping public List<OrderResponse> list() { return service.listOrders(); }
    @GetMapping("/{id}") public OrderResponse get(@PathVariable Long id) { return service.getOrder(id); }
    @PatchMapping("/{id}/status") public OrderResponse status(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) { return service.updateStatus(id, request); }
}
