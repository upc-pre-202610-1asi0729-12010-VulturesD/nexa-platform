package com.nexa.platform.iam.domain.model.repositories;

import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.RoleName;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(RoleName name);
    Role save(Role role);
}
