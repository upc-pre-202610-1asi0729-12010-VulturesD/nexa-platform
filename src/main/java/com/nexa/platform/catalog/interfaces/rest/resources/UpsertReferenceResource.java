package com.nexa.platform.catalog.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertReferenceResource(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 240) String description
) { }
