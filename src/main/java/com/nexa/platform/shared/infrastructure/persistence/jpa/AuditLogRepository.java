package com.nexa.platform.shared.infrastructure.persistence.jpa;

import com.nexa.platform.shared.domain.model.AuditLog;
import com.nexa.platform.shared.domain.repositories.AuditLogRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, AuditLogRepositoryPort {
    List<AuditLog> findByTenantIdOrderByIdDesc(Long tenantId, Pageable pageable);
    Optional<AuditLog> findByIdAndTenantId(Long id, Long tenantId);
}
