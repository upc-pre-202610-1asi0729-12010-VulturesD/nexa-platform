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
        ProfileContract profile = profileFor(roles, account.getEmail());
        return new UserResponse(account.getId(), account.getFullName(), account.getFullName(), account.getEmail(), roles,
            profile.profile(), profile.scope(), profile.segment(), profile.workspace(), profile.clientId());
    }

    private ProfileContract profileFor(Set<String> roles, String email) {
        if (roles.contains("ROLE_BUYER")) return new ProfileContract("buyer", "portal", "S3 Buyer Portal", "buyer-portal", "CLI-001");
        if (roles.contains("ROLE_WAREHOUSE")) return new ProfileContract("warehouse", "warehouse", "S2 Operations", "warehouse", null);
        if (roles.contains("ROLE_LOGISTICS") || roles.contains("ROLE_OPERATOR")) return new ProfileContract("logistics", "logistics", "S2 Operations", "logistics", null);
        if (roles.contains("ROLE_SALES") || roles.contains("ROLE_ADMIN") || email.endsWith("@nexa.com")) {
            return new ProfileContract("commercial", "commercial", "S1 Commercial", "commercial", null);
        }
        return new ProfileContract("operator", "operations", "S2 Operations", "operations", null);
    }

    private record ProfileContract(String profile, String scope, String segment, String workspace, String clientId) { }
}
