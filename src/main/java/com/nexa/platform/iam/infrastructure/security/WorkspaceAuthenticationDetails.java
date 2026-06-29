package com.nexa.platform.iam.infrastructure.security;

public record WorkspaceAuthenticationDetails(Long userId, Long tenantId, Long workspaceId, String workspaceSlug,
                                             Long membershipId, Long clientAccountId) { }
