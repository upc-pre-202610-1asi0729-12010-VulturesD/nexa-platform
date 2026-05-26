package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.infrastructure.persistence.jpa.UserAccountRepository;
import com.nexa.platform.iam.infrastructure.security.UserPrincipal;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserAccountRepository users;
    private final UserMapper mapper;

    public CurrentUserService(UserAccountRepository users, UserMapper mapper) {
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
}
