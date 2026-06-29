package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.ChangePasswordRequest;
import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.shared.application.auditing.AuditLogService;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final UserAccountRepositoryPort users;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogs;

    public ProfileService(UserAccountRepositoryPort users, PasswordEncoder passwordEncoder,
                          AuditLogService auditLogs) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.auditLogs = auditLogs;
    }

    @Transactional
    public void changePassword(Long tenantId, Long userId, ChangePasswordRequest request) {
        if (request.currentPassword() == null || request.currentPassword().isBlank()
            || request.newPassword() == null || request.newPassword().isBlank()) {
            throw new IllegalArgumentException("Current and new passwords are required.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }

        UserAccount user = users.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        validatePasswordPolicy(request.newPassword());
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must differ from the current password.");
        }

        user.changePasswordHash(passwordEncoder.encode(request.newPassword()));
        users.save(user);
        auditLogs.record(tenantId, "profile.password_changed", "UserAccount", userId, null);
    }

    private static void validatePasswordPolicy(String password) {
        boolean valid = password.length() >= 10
            && password.chars().anyMatch(Character::isUpperCase)
            && password.chars().anyMatch(Character::isLowerCase)
            && password.chars().anyMatch(Character::isDigit)
            && password.chars().anyMatch(character -> !Character.isLetterOrDigit(character));
        if (!valid) {
            throw new IllegalArgumentException(
                "Password must contain at least 10 characters, uppercase, lowercase, number, and symbol.");
        }
    }
}
