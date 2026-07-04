package com.nexa.platform.invoicing.infrastructure.persistence.jpa;

import com.nexa.platform.invoicing.domain.model.BusinessDocument;
import com.nexa.platform.invoicing.domain.model.repositories.BusinessDocumentRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDocumentRepository extends JpaRepository<BusinessDocument, Long>, BusinessDocumentRepositoryPort {
    List<BusinessDocument> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<BusinessDocument> findByIdAndTenantId(Long id, Long tenantId);
    Optional<BusinessDocument> findByTenantIdAndOrderIdAndType(Long tenantId, Long orderId, String type);
}
