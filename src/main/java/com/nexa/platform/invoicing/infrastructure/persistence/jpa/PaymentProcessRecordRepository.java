package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.PaymentProcessRecord;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentProcessRecordRepositoryPort;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentProcessRecordRepository extends JpaRepository<PaymentProcessRecord, Long>, PaymentProcessRecordRepositoryPort {
    List<PaymentProcessRecord> findByTenantIdOrderByIdAsc(Long tenantId);
}
