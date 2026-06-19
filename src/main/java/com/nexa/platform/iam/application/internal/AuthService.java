package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.*;
import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.RoleName;
import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.RoleRepositoryPort;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.iam.infrastructure.security.JwtService;
import com.nexa.platform.shared.domain.exceptions.BusinessRuleException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserAccountRepositoryPort users;
    private final RoleRepositoryPort roles;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper mapper;

    public AuthService(UserAccountRepositoryPort users, RoleRepositoryPort roles, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, UserMapper mapper) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered");
        }
        Role role = roles.findByName(RoleName.ROLE_OPERATOR).orElseGet(() -> roles.save(new Role(RoleName.ROLE_OPERATOR)));
        UserAccount account = new UserAccount(request.fullName(), request.email(), passwordEncoder.encode(request.password()));
        account.addRole(role);
        UserAccount saved = users.save(account);
        return new AuthResponse(jwtService.generateToken(saved.getEmail()), "Bearer", mapper.toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount account = users.findByEmail(request.email()).orElseThrow(() -> new BusinessRuleException("Invalid credentials"));
        return new AuthResponse(jwtService.generateToken(account.getEmail()), "Bearer", mapper.toResponse(account));
    }
}
