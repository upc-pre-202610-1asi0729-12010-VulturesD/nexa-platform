package com.nexa.platform.iam.interfaces.rest.resources;

import java.util.Set;

public record UserResource(Long id, String fullName, String displayName, String email, Set<String> roles,
                           String profile, String scope, String segment, String workspace, String clientId) { }
