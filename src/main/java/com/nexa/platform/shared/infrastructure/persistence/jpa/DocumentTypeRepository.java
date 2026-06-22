package com.nexa.platform.shared.infrastructure.persistence.jpa;

import com.nexa.platform.shared.domain.model.DocumentType;
import com.nexa.platform.shared.domain.repositories.DocumentTypeRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long>, DocumentTypeRepositoryPort {
    List<DocumentType> findAllByOrderByIdAsc();
    Optional<DocumentType> findByKeyAndActiveTrue(String key);
}
