package com.nexa.platform.catalog.infrastructure.persistence.jpa;

import com.nexa.platform.catalog.domain.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    List<Product> findByActiveTrueOrderByNameAsc();
    List<Product> findByActiveTrueOrderByIdAsc();
    boolean existsBySku(String sku);
}
