package com.nexa.platform.invoicing.application.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record InvoiceLineRequest(@NotBlank String description, @Min(1) int quantity, @NotNull @DecimalMin("0.0") BigDecimal unitPrice) { }
