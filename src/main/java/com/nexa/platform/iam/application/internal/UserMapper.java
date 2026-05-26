package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.UserResponse;
import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.UserAccount;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(UserAccount account) {
        Set<String> roles = account.getRoles().stream()
            .map(Role::getName)
            .map(Enum::name)
            .collect(Collectors.toSet());
        return new UserResponse(account.getId(), account.getFullName(), account.getEmail(), roles);
    }
}
