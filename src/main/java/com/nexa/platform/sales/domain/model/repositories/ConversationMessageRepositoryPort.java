package com.nexa.platform.sales.domain.model.repositories;

import com.nexa.platform.sales.domain.model.ConversationMessage;
import java.util.List;
import java.util.Optional;

public interface ConversationMessageRepositoryPort {
    List<ConversationMessage> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<ConversationMessage> findByIdAndTenantId(Long id, Long tenantId);
    ConversationMessage save(ConversationMessage message);
    void delete(ConversationMessage message);
}
