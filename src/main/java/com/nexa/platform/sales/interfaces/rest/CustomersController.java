package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.interfaces.rest.resources.CustomerResource;
import com.nexa.platform.sales.interfaces.rest.transform.CustomerResourceFromEntityAssembler;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomersController {
    private final SalesService service;
    public CustomersController(SalesService service) { this.service = service; }
    @GetMapping public List<CustomerResource> list() {
        return service.listCustomers().stream().map(CustomerResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}
