package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    Optional<Invoice> findById(Long id);
    Optional<Invoice> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Invoice> findByTenantIdAndInvoiceNumber(Long tenantId, String invoiceNumber);
    List<Invoice> findAll();
    List<Invoice> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Invoice> findByOrderIdOrderByIdAsc(Long orderId);
    List<Invoice> findByTenantIdAndOrderIdOrderByIdAsc(Long tenantId, Long orderId);
    Invoice save(Invoice invoice);
}
