package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.PaymentProcessRecord;
import java.util.List;
import java.util.Optional;

public interface PaymentProcessRecordRepositoryPort {
    List<PaymentProcessRecord> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<PaymentProcessRecord> findByIdAndTenantId(Long id, Long tenantId);
    PaymentProcessRecord save(PaymentProcessRecord record);
}
