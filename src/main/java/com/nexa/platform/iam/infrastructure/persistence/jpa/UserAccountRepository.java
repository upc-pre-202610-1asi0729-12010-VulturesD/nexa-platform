package com.nexa.platform.iam.infrastructure.persistence.jpa;

import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, UserAccountRepositoryPort {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
