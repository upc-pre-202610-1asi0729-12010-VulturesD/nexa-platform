package com.nexa.platform.iam.application.dtos;

import java.util.Set;

public record UserResponse(
    Long id,
    String fullName,
    String displayName,
    String email,
    Set<String> roles,
    String profile,
    String scope,
    String segment,
    String workspace,
    String clientId,
    Long tenantId,
    Long workspaceId,
    String workspaceSlug,
    String workspaceStatus,
    String membershipStatus,
    Long clientAccountId,
    String phone,
    String preferredLanguage,
    boolean criticalNotificationsEnabled
) { }
