package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record ChangeInvoiceStatusResource(@NotBlank String paymentStatus) { }
