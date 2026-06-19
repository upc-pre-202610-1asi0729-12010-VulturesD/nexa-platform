package com.nexa.platform.iam.infrastructure.persistence.jpa;

import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.RoleName;
import com.nexa.platform.iam.domain.model.repositories.RoleRepositoryPort;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryPort {
    Optional<Role> findByName(RoleName name);
}
