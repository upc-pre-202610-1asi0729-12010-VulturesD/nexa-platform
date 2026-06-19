package com.nexa.platform.invoicing.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateInvoiceRequest(@NotNull Long orderId, @NotEmpty List<@Valid InvoiceLineRequest> lines) { }
