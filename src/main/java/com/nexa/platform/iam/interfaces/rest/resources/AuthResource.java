package com.nexa.platform.iam.interfaces.rest.resources;

public record AuthResource(String token, String tokenType, UserResource user) { }
