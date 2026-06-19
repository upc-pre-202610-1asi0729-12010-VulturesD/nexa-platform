package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.InvoiceResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.InvoiceResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoicesController {
    private final InvoicingService service;
    public InvoicesController(InvoicingService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public InvoiceResource create(@Valid @RequestBody InvoiceResource resource) {
        return InvoiceResourceAssembler.toResourceFromEntity(service.createInvoice(InvoiceResourceAssembler.toRequestFromResource(resource)));
    }
    @GetMapping public List<InvoiceResource> list() {
        return service.listInvoices().stream().map(InvoiceResourceAssembler::toResourceFromEntity).toList();
    }
    @GetMapping("/{id}") public InvoiceResource get(@PathVariable Long id) {
        return InvoiceResourceAssembler.toResourceFromEntity(service.getInvoice(id));
    }
}
