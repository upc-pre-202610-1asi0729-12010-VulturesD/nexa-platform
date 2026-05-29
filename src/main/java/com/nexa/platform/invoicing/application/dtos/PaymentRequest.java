package com.nexa.platform.invoicing.application.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentRequest(@NotNull Long invoiceId, @NotNull @DecimalMin("0.01") BigDecimal amount, @NotBlank String method) { }
