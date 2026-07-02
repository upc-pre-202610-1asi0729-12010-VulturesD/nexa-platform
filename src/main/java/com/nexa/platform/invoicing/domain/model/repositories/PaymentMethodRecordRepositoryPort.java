package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.PaymentMethodRecord;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodRecordRepositoryPort {
    List<PaymentMethodRecord> findByTenantIdOrderByClientAccountIdAscIsDefaultDesc(Long tenantId);
    List<PaymentMethodRecord> findByTenantIdAndClientAccountIdAndIsDefaultTrue(Long tenantId, Long clientAccountId);
    Optional<PaymentMethodRecord> findByIdAndTenantId(Long id, Long tenantId);
    PaymentMethodRecord save(PaymentMethodRecord record);
}
