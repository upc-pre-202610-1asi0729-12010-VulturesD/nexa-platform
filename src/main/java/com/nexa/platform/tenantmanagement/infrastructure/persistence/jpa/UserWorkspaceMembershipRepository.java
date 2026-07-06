package com.nexa.platform.tenantmanagement.infrastructure.persistence.jpa;

import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWorkspaceMembershipRepository extends JpaRepository<UserWorkspaceMembership, Long>, UserWorkspaceMembershipRepositoryPort {
    List<UserWorkspaceMembership> findByTenantIdOrderByIdAsc(Long tenantId);
    List<UserWorkspaceMembership> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
    Optional<UserWorkspaceMembership> findFirstByUserIdAndStatusOrderByIdAsc(Long userId, String status);
    Optional<UserWorkspaceMembership> findByUserIdAndWorkspaceIdAndStatus(Long userId, Long workspaceId, String status);
}
