package com.nexa.platform.catalog.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductResource(
    @NotBlank @Size(max = 40) String sku,
    @NotBlank @Size(max = 140) String name,
    @Size(max = 360) String description,
    @NotNull Long categoryId,
    @NotBlank @Size(max = 120) String supplierName,
    @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
    @Size(max = 32) String unit,
    @Size(max = 280) String imageUrl,
    Integer minCelsius,
    Integer maxCelsius,
    @Size(max = 240) String handlingNotes
) { }
