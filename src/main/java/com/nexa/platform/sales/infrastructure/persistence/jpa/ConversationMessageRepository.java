package com.nexa.platform.sales.infrastructure.persistence.jpa;

import com.nexa.platform.sales.domain.model.ConversationMessage;
import com.nexa.platform.sales.domain.model.repositories.ConversationMessageRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long>,
    ConversationMessageRepositoryPort {
    List<ConversationMessage> findByTenantIdOrderByIdAsc(Long tenantId);
    Optional<ConversationMessage> findByIdAndTenantId(Long id, Long tenantId);
}
