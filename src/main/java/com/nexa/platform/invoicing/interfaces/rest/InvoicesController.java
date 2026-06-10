package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.application.internal.InvoicingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoicesController {
    private final InvoicingService service;
    public InvoicesController(InvoicingService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public InvoiceResponse create(@Valid @RequestBody CreateInvoiceRequest request) { return service.createInvoice(request); }
    @GetMapping public List<InvoiceResponse> list() { return service.listInvoices(); }
    @GetMapping("/{id}") public InvoiceResponse get(@PathVariable Long id) { return service.getInvoice(id); }
}
