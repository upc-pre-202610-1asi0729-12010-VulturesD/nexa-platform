package com.nexa.platform.iam.infrastructure.persistence.jpa;

import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
