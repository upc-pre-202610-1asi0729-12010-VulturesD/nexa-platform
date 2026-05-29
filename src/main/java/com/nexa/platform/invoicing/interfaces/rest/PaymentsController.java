package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.dtos.*;
import com.nexa.platform.invoicing.application.internal.InvoicingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {
    private final InvoicingService service;
    public PaymentsController(InvoicingService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PaymentResponse create(@Valid @RequestBody PaymentRequest request) { return service.registerPayment(request); }
}
