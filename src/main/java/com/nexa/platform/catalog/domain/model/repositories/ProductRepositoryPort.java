package com.nexa.platform.catalog.domain.model.repositories;

import com.nexa.platform.catalog.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    List<Product> findByActiveTrueOrderByIdAsc();
    boolean existsBySku(String sku);
    Product save(Product product);
}
