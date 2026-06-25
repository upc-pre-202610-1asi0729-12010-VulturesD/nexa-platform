package com.nexa.platform.iam.domain.model.repositories;

import com.nexa.platform.iam.domain.model.UserAccount;
import java.util.List;
import java.util.Optional;

public interface UserAccountRepositoryPort {
    Optional<UserAccount> findById(Long id);
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserAccount> findAll();
    UserAccount save(UserAccount account);
}
