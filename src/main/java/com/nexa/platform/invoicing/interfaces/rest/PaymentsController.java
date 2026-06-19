package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.PaymentResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.PaymentResourceAssembler;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {
    private final InvoicingService service;
    public PaymentsController(InvoicingService service) { this.service = service; }
    @GetMapping public List<PaymentResource> list() {
        return service.listPayments().stream().map(PaymentResourceAssembler::toResourceFromEntity).toList();
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PaymentResource create(@Valid @RequestBody PaymentResource resource) {
        return PaymentResourceAssembler.toResourceFromEntity(service.registerPayment(PaymentResourceAssembler.toRequestFromResource(resource)));
    }
}
