package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.NotificationRecord;
import java.util.List;
import java.util.Optional;

public interface NotificationRecordRepositoryPort {
    List<NotificationRecord> findByTenantIdOrderByReadAscIdDesc(Long tenantId);
    List<NotificationRecord> findByTenantIdAndClientAccountIdOrderByReadAscIdDesc(Long tenantId, Long clientAccountId);
    Optional<NotificationRecord> findByIdAndTenantId(Long id, Long tenantId);
    Optional<NotificationRecord> findByIdAndTenantIdAndClientAccountId(Long id, Long tenantId, Long clientAccountId);
    NotificationRecord save(NotificationRecord notification);
    void delete(NotificationRecord notification);
}
