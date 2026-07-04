package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {
    List<Payment> findAll();
    List<Payment> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Payment> findByTenantIdAndInvoiceIdOrderByIdAsc(Long tenantId, Long invoiceId);
    List<Payment> findByInvoiceOrderIdOrderByIdAsc(Long orderId);
    List<Payment> findByTenantIdAndInvoiceOrderIdOrderByIdAsc(Long tenantId, Long orderId);
    Optional<Payment> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Payment> findByReferenceCode(String referenceCode);
    Payment save(Payment payment);
}
