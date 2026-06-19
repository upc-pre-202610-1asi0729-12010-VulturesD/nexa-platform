package com.nexa.platform.catalog.domain.model.repositories;

import com.nexa.platform.catalog.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    Category save(Category category);
}
