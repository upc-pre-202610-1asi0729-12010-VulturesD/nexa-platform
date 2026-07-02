package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.PaymentMethodRecord;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentMethodRecordRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRecordRepository
    extends JpaRepository<PaymentMethodRecord, Long>, PaymentMethodRecordRepositoryPort {
    List<PaymentMethodRecord> findByTenantIdOrderByClientAccountIdAscIsDefaultDesc(Long tenantId);
    List<PaymentMethodRecord> findByTenantIdAndClientAccountIdAndIsDefaultTrue(Long tenantId, Long clientAccountId);
}
