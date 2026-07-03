package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.Payment;
import com.nexa.platform.invoicing.domain.model.repositories.PaymentRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryPort {
    List<Payment> findByInvoiceOrderIdOrderByIdAsc(Long orderId);
    List<Payment> findByTenantIdAndInvoiceOrderIdOrderByIdAsc(Long tenantId, Long orderId);
    List<Payment> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Payment> findByTenantIdAndInvoiceIdOrderByIdAsc(Long tenantId, Long invoiceId);
    Optional<Payment> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Payment> findByReferenceCode(String referenceCode);
}
