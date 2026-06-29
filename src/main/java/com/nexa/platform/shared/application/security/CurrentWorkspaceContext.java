package com.nexa.platform.shared.application.security;

import com.nexa.platform.iam.infrastructure.security.UserPrincipal;
import com.nexa.platform.iam.infrastructure.security.WorkspaceAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentWorkspaceContext {
    public Long userId() {
        Authentication authentication = authentication();
        if (authentication.getPrincipal() instanceof UserPrincipal principal) return principal.id();
        return null;
    }

    public Long tenantId() {
        WorkspaceAuthenticationDetails details = details();
        return details == null ? null : details.tenantId();
    }

    public Long workspaceId() {
        WorkspaceAuthenticationDetails details = details();
        return details == null ? null : details.workspaceId();
    }

    public String workspaceSlug() {
        WorkspaceAuthenticationDetails details = details();
        return details == null ? null : details.workspaceSlug();
    }

    public Long membershipId() {
        WorkspaceAuthenticationDetails details = details();
        return details == null ? null : details.membershipId();
    }

    public Long clientAccountId() {
        WorkspaceAuthenticationDetails details = details();
        return details == null ? null : details.clientAccountId();
    }

    public Long requireTenant(Long requestedTenantId) {
        Long authenticatedTenant = tenantId();
        if (authenticatedTenant != null) {
            if (requestedTenantId != null && !authenticatedTenant.equals(requestedTenantId)) {
                throw new WorkspaceScopeException("Tenant header does not match authenticated workspace.");
            }
            return authenticatedTenant;
        }
        if (authentication().getPrincipal() instanceof UserPrincipal) {
            throw new WorkspaceScopeException("Authenticated workspace membership is required.");
        }
        if (requestedTenantId == null || requestedTenantId <= 0) {
            throw new IllegalArgumentException("Current tenant is required.");
        }
        return requestedTenantId;
    }

    private WorkspaceAuthenticationDetails details() {
        Object details = authentication().getDetails();
        return details instanceof WorkspaceAuthenticationDetails workspace ? workspace : null;
    }

    private Authentication authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new WorkspaceScopeException("Authenticated workspace is required.");
        }
        return authentication;
    }
}
