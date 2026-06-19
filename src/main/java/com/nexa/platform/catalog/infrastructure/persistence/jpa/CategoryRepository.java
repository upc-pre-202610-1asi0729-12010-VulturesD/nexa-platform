package com.nexa.platform.catalog.infrastructure.persistence.jpa;

import com.nexa.platform.catalog.domain.model.Category;
import com.nexa.platform.catalog.domain.model.repositories.CategoryRepositoryPort;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryPort {
    Optional<Category> findByName(String name);
}
