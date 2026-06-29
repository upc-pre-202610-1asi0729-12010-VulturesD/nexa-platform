package com.nexa.platform.shared.domain.repositories;

import com.nexa.platform.shared.domain.model.DocumentType;
import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepositoryPort {
    List<DocumentType> findAllByOrderByIdAsc();
    Optional<DocumentType> findById(Long id);
    Optional<DocumentType> findByKeyAndActiveTrue(String key);
    DocumentType save(DocumentType documentType);
}
