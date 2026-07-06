package com.nexa.platform.tenantmanagement.domain.model.repositories;

import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import java.util.List;
import java.util.Optional;

public interface UserWorkspaceMembershipRepositoryPort {
    List<UserWorkspaceMembership> findByTenantIdOrderByIdAsc(Long tenantId);
    List<UserWorkspaceMembership> findByWorkspaceIdOrderByIdAsc(Long workspaceId);
    Optional<UserWorkspaceMembership> findById(Long id);
    Optional<UserWorkspaceMembership> findFirstByUserIdAndStatusOrderByIdAsc(Long userId, String status);
    Optional<UserWorkspaceMembership> findByUserIdAndWorkspaceIdAndStatus(Long userId, Long workspaceId, String status);
    UserWorkspaceMembership save(UserWorkspaceMembership membership);
    void delete(UserWorkspaceMembership membership);
}
