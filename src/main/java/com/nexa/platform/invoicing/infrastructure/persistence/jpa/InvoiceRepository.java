package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.Invoice;
import com.nexa.platform.invoicing.domain.model.repositories.InvoiceRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, InvoiceRepositoryPort {
    Optional<Invoice> findByTenantIdAndInvoiceNumber(Long tenantId, String invoiceNumber);
    List<Invoice> findByTenantIdOrderByIdAsc(Long tenantId);
    List<Invoice> findByOrderIdOrderByIdAsc(Long orderId);
    List<Invoice> findByTenantIdAndOrderIdOrderByIdAsc(Long tenantId, Long orderId);
}
