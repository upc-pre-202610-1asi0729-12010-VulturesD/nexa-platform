package com.nexa.platform.iam.infrastructure.security;

import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserPrincipalService userPrincipalService;
    private final UserWorkspaceMembershipRepositoryPort memberships;
    private final WorkspaceRepositoryPort workspaces;

    public JwtAuthenticationFilter(JwtService jwtService, UserPrincipalService userPrincipalService,
                                   UserWorkspaceMembershipRepositoryPort memberships,
                                   WorkspaceRepositoryPort workspaces) {
        this.jwtService = jwtService;
        this.userPrincipalService = userPrincipalService;
        this.memberships = memberships;
        this.workspaces = workspaces;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);
        try {
            Claims claims = jwtService.extractClaims(token);
            String email = claims.getSubject();
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails details = userPrincipalService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                WorkspaceAuthenticationDetails workspaceDetails = workspaceDetails(claims, details);
                if (workspaceDetails != null) {
                    String tenantHeader = request.getHeader("X-Nexa-Tenant-Id");
                    String workspaceHeader = request.getHeader("X-Nexa-Workspace");
                    if (tenantHeader != null && !tenantHeader.isBlank()
                        && !String.valueOf(workspaceDetails.tenantId()).equals(tenantHeader.trim())) {
                        forbidden(response, "Tenant header does not match authenticated workspace.");
                        return;
                    }
                    if (workspaceHeader != null && !workspaceHeader.isBlank()
                        && !workspaceDetails.workspaceSlug().equalsIgnoreCase(workspaceHeader.trim())) {
                        forbidden(response, "Workspace header does not match authenticated workspace.");
                        return;
                    }
                    authentication.setDetails(workspaceDetails);
                } else {
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (RuntimeException ignored) {
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }

    private WorkspaceAuthenticationDetails workspaceDetails(Claims claims, UserDetails details) {
        Long tenantId = claimLong(claims, "tenant_id");
        Long workspaceId = claimLong(claims, "workspace_id");
        Long membershipId = claimLong(claims, "membership_id");
        Long userId = claimLong(claims, "user_id");
        if (tenantId == null || workspaceId == null || membershipId == null || userId == null) return null;
        if (!(details instanceof UserPrincipal principal) || !principal.id().equals(userId)) {
            throw new IllegalArgumentException("Invalid workspace user claim.");
        }
        var membership = memberships.findById(membershipId)
            .filter(candidate -> candidate.getUserId().equals(userId)
                && candidate.getTenantId().equals(tenantId)
                && candidate.getWorkspaceId().equals(workspaceId)
                && "active".equalsIgnoreCase(candidate.getStatus()))
            .orElseThrow(() -> new IllegalArgumentException("Workspace membership is not active."));
        var workspace = workspaces.findById(workspaceId)
            .filter(candidate -> candidate.getTenantId().equals(tenantId)
                && "active".equalsIgnoreCase(candidate.getStatus()))
            .orElseThrow(() -> new IllegalArgumentException("Workspace is not active."));
        return new WorkspaceAuthenticationDetails(userId, tenantId, workspaceId, workspace.getSlug(),
            membershipId, membership.getClientAccountId());
    }

    private static Long claimLong(Claims claims, String key) {
        Object value = claims.get(key);
        if (value instanceof Number number) return number.longValue();
        if (value instanceof String text && text.matches("\\d+")) return Long.valueOf(text);
        return null;
    }

    private static void forbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
