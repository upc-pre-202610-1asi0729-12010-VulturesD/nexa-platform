package com.nexa.platform.shared.domain.repositories;

import com.nexa.platform.shared.domain.model.AuditLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface AuditLogRepositoryPort {
    List<AuditLog> findByTenantIdOrderByIdDesc(Long tenantId, Pageable pageable);
    Optional<AuditLog> findByIdAndTenantId(Long id, Long tenantId);
    AuditLog save(AuditLog auditLog);
}
