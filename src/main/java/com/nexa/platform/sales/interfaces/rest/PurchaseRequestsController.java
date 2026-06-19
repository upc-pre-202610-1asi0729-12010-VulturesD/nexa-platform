package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.sales.application.internal.SalesService;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestResource;
import com.nexa.platform.sales.interfaces.rest.transform.PurchaseRequestResourceFromEntityAssembler;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/purchase-requests", "/api/v1/requests"})
public class PurchaseRequestsController {
    private final SalesService service;

    public PurchaseRequestsController(SalesService service) { this.service = service; }

    @GetMapping public List<PurchaseRequestResource> list() {
        return service.listPurchaseRequests().stream().map(PurchaseRequestResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public PurchaseRequestResource get(@PathVariable String id) {
        return PurchaseRequestResourceFromEntityAssembler.toResourceFromEntity(service.getPurchaseRequest(id));
    }
}
