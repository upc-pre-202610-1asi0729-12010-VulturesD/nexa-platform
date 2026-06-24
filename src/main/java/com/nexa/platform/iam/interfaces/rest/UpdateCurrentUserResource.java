package com.nexa.platform.iam.interfaces.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCurrentUserResource(
    @NotBlank @Size(max = 120) String fullName,
    @Email @Size(max = 160) String email,
    @Size(max = 32) String phone,
    @NotBlank @Pattern(regexp = "en|es") String preferredLanguage,
    boolean criticalNotificationsEnabled
) { }
