package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceResource(Long id, Long tenantId, String invoiceNumber, @NotNull Long orderId,
                              String currency, String status, BigDecimal total, OffsetDateTime paidAt,
                              @NotEmpty List<@Valid InvoiceLineResource> lines) { }
