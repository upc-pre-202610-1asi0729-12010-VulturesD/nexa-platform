package com.nexa.platform.catalog.domain.model.repositories;

import com.nexa.platform.catalog.domain.model.Brand;
import java.util.List;
import java.util.Optional;

public interface BrandRepositoryPort {
    Optional<Brand> findById(Long id);
    Optional<Brand> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Brand> findAllByOrderByNameAsc();
    Brand save(Brand brand);
}
