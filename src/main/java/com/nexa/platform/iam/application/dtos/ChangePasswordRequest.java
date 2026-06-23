package com.nexa.platform.iam.application.dtos;

public record ChangePasswordRequest(
    String currentPassword,
    String newPassword,
    String confirmPassword
) { }
