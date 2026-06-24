package com.nexa.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordResource(
    @NotNull @Size(max = 80) String currentPassword,
    @NotNull @Size(max = 80) String newPassword,
    @NotNull @Size(max = 80) String confirmPassword
) { }
