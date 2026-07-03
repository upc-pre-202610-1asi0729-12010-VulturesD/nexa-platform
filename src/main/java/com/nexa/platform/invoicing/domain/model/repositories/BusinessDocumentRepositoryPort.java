package com.nexa.platform.invoicing.domain.model.repositories;

import com.nexa.platform.invoicing.domain.model.BusinessDocument;
import java.util.List;
import java.util.Optional;

public interface BusinessDocumentRepositoryPort {
    List<BusinessDocument> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<BusinessDocument> findByIdAndTenantId(Long id, Long tenantId);
    Optional<BusinessDocument> findByTenantIdAndOrderIdAndType(Long tenantId, Long orderId, String type);
    BusinessDocument save(BusinessDocument document);
}
