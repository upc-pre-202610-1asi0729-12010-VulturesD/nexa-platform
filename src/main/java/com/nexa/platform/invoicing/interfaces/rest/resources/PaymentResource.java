package com.nexa.platform.invoicing.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentResource(String id, Long backendId, @NotNull Long invoiceId, String invoiceCode, String orderId,
                              String referenceCode, @NotNull @DecimalMin("0.01") BigDecimal amount, String currency,
                              String status, @NotBlank String method) { }
