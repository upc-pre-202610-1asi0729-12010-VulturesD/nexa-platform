package com.nexa.platform.iam.application.dtos;

public record AuthResponse(String token, String tokenType, UserResponse user) { }
