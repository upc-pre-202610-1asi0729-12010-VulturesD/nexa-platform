package com.nexa.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterResource(
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Size(min = 8, max = 80) String password
) { }
