package com.nexa.platform.catalog.infrastructure.persistence.jpa;

import com.nexa.platform.catalog.domain.model.Brand;
import com.nexa.platform.catalog.domain.model.repositories.BrandRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long>, BrandRepositoryPort {
    Optional<Brand> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Brand> findAllByOrderByNameAsc();
}
