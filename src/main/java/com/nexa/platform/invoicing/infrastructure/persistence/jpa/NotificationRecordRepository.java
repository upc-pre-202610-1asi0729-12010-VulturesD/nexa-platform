package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.NotificationRecord;
import com.nexa.platform.invoicing.domain.model.repositories.NotificationRecordRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecordRepository
    extends JpaRepository<NotificationRecord, Long>, NotificationRecordRepositoryPort {
    List<NotificationRecord> findByTenantIdOrderByReadAscIdDesc(Long tenantId);
    List<NotificationRecord> findByTenantIdAndClientAccountIdOrderByReadAscIdDesc(Long tenantId, Long clientAccountId);
    Optional<NotificationRecord> findByIdAndTenantId(Long id, Long tenantId);
    Optional<NotificationRecord> findByIdAndTenantIdAndClientAccountId(Long id, Long tenantId, Long clientAccountId);
}
