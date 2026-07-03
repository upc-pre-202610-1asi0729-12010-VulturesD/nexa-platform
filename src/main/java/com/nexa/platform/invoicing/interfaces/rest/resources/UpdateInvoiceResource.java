package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateInvoiceResource(String invoiceNumber, @NotNull Long orderId, String currency,
                                    @NotEmpty List<@Valid InvoiceLineResource> lines) { }
