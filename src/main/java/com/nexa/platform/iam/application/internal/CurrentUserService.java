package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.iam.infrastructure.security.UserPrincipal;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private static final Set<String> WORKSPACE_EMAILS = Set.of(
        "sales@nexa.com",
        "logistics@nexa.com",
        "buyer@nexa.com"
    );

    private final UserAccountRepositoryPort users;
    private final UserMapper mapper;

    public CurrentUserService(UserAccountRepositoryPort users, UserMapper mapper) {
        this.users = users;
        this.mapper = mapper;
    }

    public UserResponse currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("Current user", "anonymous");
        }
        return users.findById(principal.id()).map(mapper::toResponse).orElseThrow(() -> new ResourceNotFoundException("User", principal.id()));
    }

    public List<UserResponse> listWorkspaceUsers() {
        return users.findAll().stream()
            .filter(account -> WORKSPACE_EMAILS.contains(account.getEmail()))
            .map(mapper::toResponse)
            .toList();
    }
}
