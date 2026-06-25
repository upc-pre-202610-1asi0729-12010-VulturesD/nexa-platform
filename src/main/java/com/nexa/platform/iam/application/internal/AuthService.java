package com.nexa.platform.iam.application.internal;

import com.nexa.platform.iam.application.dtos.*;
import com.nexa.platform.iam.domain.model.Role;
import com.nexa.platform.iam.domain.model.RoleName;
import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.RoleRepositoryPort;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.iam.infrastructure.security.JwtService;
import com.nexa.platform.shared.domain.exceptions.BusinessRuleException;
import com.nexa.platform.tenantmanagement.domain.model.Tenant;
import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import com.nexa.platform.tenantmanagement.domain.model.Workspace;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final TenantRepositoryPort tenants;
    private final WorkspaceRepositoryPort workspaces;
    private final UserWorkspaceMembershipRepositoryPort memberships;

    public AuthService(UserAccountRepositoryPort users, RoleRepositoryPort roles, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, UserMapper mapper,
                       TenantRepositoryPort tenants, WorkspaceRepositoryPort workspaces,
                       UserWorkspaceMembershipRepositoryPort memberships) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.mapper = mapper;
        this.tenants = tenants;
        this.workspaces = workspaces;
        this.memberships = memberships;
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

    @Transactional
    public UserResponse createWorkspaceUser(String username, String email, String password, String roleName,
                                            String fullName) {
        if (users.existsByEmail(email)) throw new BusinessRuleException("Email already registered");
        RoleName role = workspaceRole(roleName);
        Role assignedRole = roles.findByName(role).orElseGet(() -> roles.save(new Role(role)));
        String resolvedName = fullName == null || fullName.isBlank() ? username.trim() : fullName.trim();
        UserAccount account = new UserAccount(resolvedName, email.trim().toLowerCase(), passwordEncoder.encode(password));
        account.addRole(assignedRole);
        return mapper.toResponse(users.save(account));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserAccount account = users.findByEmail(request.email()).orElseThrow(() -> new BusinessRuleException("Invalid credentials"));
        WorkspaceSession session = resolveWorkspaceSession(account, request.workspaceSlug());
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", account.getId());
        claims.put("tenant_id", session.membership().getTenantId());
        claims.put("workspace_id", session.workspace().getId());
        claims.put("workspace_slug", session.workspace().getSlug());
        claims.put("membership_id", session.membership().getId());
        if (session.membership().getClientAccountId() != null) {
            claims.put("client_account_id", session.membership().getClientAccountId());
        }
        return new AuthResponse(jwtService.generateToken(account.getEmail(), claims), "Bearer",
            mapper.toResponse(account, session.workspace(), session.membership()));
    }

    private WorkspaceSession resolveWorkspaceSession(UserAccount account, String requestedSlug) {
        UserWorkspaceMembership membership;
        Workspace workspace;
        if (requestedSlug != null && !requestedSlug.isBlank()) {
            workspace = workspaces.findBySlug(requestedSlug.trim().toLowerCase())
                .filter(candidate -> "active".equalsIgnoreCase(candidate.getStatus()))
                .orElseThrow(AuthService::invalidCredentials);
            membership = memberships.findByUserIdAndWorkspaceIdAndStatus(account.getId(), workspace.getId(), "active")
                .orElseThrow(AuthService::invalidCredentials);
        } else {
            membership = memberships.findFirstByUserIdAndStatusOrderByIdAsc(account.getId(), "active")
                .orElseThrow(AuthService::invalidCredentials);
            workspace = workspaces.findById(membership.getWorkspaceId())
                .filter(candidate -> "active".equalsIgnoreCase(candidate.getStatus()))
                .orElseThrow(AuthService::invalidCredentials);
        }
        Tenant tenant = tenants.findById(membership.getTenantId())
            .filter(candidate -> "active".equalsIgnoreCase(candidate.getStatus()))
            .orElseThrow(AuthService::invalidCredentials);
        if (!tenant.getId().equals(workspace.getTenantId())) {
            throw invalidCredentials();
        }
        return new WorkspaceSession(workspace, membership);
    }

    private static BadCredentialsException invalidCredentials() {
        return new BadCredentialsException("Invalid credentials");
    }

    private static RoleName workspaceRole(String role) {
        return switch (role == null ? "" : role.trim().toLowerCase()) {
            case "company owner", "owner", "admin", "administrator" -> RoleName.ROLE_ADMIN;
            case "sales", "commercial", "commercial coordinator" -> RoleName.ROLE_SALES;
            case "logistics manager", "logistics" -> RoleName.ROLE_LOGISTICS;
            case "b2b buyer", "buyer" -> RoleName.ROLE_BUYER;
            case "viewer" -> RoleName.ROLE_VIEWER;
            case "operator" -> RoleName.ROLE_OPERATOR;
            default -> throw new BusinessRuleException("Unsupported workspace role");
        };
    }

    private record WorkspaceSession(Workspace workspace, UserWorkspaceMembership membership) { }
}
