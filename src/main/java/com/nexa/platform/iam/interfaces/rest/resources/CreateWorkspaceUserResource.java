package com.nexa.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceUserResource(
    @NotBlank @Size(max = 80) String username,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Size(min = 8, max = 80) String password,
    @NotBlank @Size(max = 60) String role,
    @Size(max = 120) String fullName
) { }
