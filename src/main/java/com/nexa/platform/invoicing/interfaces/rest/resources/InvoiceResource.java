package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record InvoiceResource(Long id, String invoiceNumber, @NotNull Long orderId, String status,
                              BigDecimal total, @NotEmpty List<@Valid InvoiceLineResource> lines) { }
