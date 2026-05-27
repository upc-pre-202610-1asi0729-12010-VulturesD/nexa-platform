package com.nexa.platform.catalog.application.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank @Size(max = 40) String sku,
    @NotBlank @Size(max = 140) String name,
    @Size(max = 360) String description,
    @NotNull Long categoryId,
    @NotBlank @Size(max = 120) String supplierName,
    @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
    Integer minCelsius,
    Integer maxCelsius,
    @Size(max = 240) String handlingNotes
) { }
