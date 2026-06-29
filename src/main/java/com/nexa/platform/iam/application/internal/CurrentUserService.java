package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.iam.infrastructure.security.UserPrincipal;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrentUserService {
    private final UserAccountRepositoryPort users;
    private final UserWorkspaceMembershipRepositoryPort memberships;
    private final WorkspaceRepositoryPort workspaces;
    private final UserMapper mapper;

    public CurrentUserService(UserAccountRepositoryPort users,
                              UserWorkspaceMembershipRepositoryPort memberships,
                              WorkspaceRepositoryPort workspaces,
                              UserMapper mapper) {
        this.users = users;
        this.memberships = memberships;
        this.workspaces = workspaces;
        this.mapper = mapper;
    }

    public UserResponse currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("Current user", "anonymous");
        }
        return users.findById(principal.id()).map(mapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("User", principal.id()));
    }

    @Transactional
    public UserResponse updateCurrentUser(String fullName, String email, String phone,
                                          String preferredLanguage, boolean criticalNotificationsEnabled) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("Current user", "anonymous");
        }
        var account = users.findById(principal.id())
            .orElseThrow(() -> new ResourceNotFoundException("User", principal.id()));
        String normalizedEmail = email == null || email.isBlank() ? account.getEmail() : email.trim().toLowerCase();
        users.findByEmail(normalizedEmail)
            .filter(existing -> !existing.getId().equals(account.getId()))
            .ifPresent(existing -> { throw new IllegalArgumentException("Email is already registered."); });
        account.updateProfile(fullName, normalizedEmail, phone, preferredLanguage, criticalNotificationsEnabled);
        return mapper.toResponse(users.save(account));
    }

    public List<UserResponse> listWorkspaceUsers(Long tenantId) {
        Map<Long, UserWorkspaceMembership> membershipByUser = new LinkedHashMap<>();
        memberships.findByTenantIdOrderByIdAsc(tenantId)
            .forEach(membership -> membershipByUser.putIfAbsent(membership.getUserId(), membership));
        return membershipByUser.values().stream()
            .map(this::toScopedResponse)
            .flatMap(java.util.Optional::stream)
            .toList();
    }

    public UserResponse workspaceUser(Long tenantId, Long userId) {
        UserWorkspaceMembership membership = memberships.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(candidate -> candidate.getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return toScopedResponse(membership)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private java.util.Optional<UserResponse> toScopedResponse(UserWorkspaceMembership membership) {
        return users.findById(membership.getUserId()).map(account -> mapper.toResponse(
            account,
            workspaces.findById(membership.getWorkspaceId())
                .filter(workspace -> workspace.getTenantId().equals(membership.getTenantId()))
                .orElse(null),
            membership));
    }
}
