package com.nexa.platform.iam.application.dtos;

import java.util.Set;

public record UserResponse(Long id, String fullName, String email, Set<String> roles) { }
