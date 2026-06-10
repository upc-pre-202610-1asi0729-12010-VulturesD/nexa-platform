package com.nexa.platform.catalog.application.internal;

import com.nexa.platform.catalog.application.dtos.*;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.infrastructure.persistence.jpa.*;
import com.nexa.platform.shared.domain.exceptions.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final CatalogMapper mapper;

    public CatalogService(ProductRepository products, CategoryRepository categories, CatalogMapper mapper) {
        this.products = products; this.categories = categories; this.mapper = mapper;
    }

    public List<ProductResponse> listProducts() { return products.findByActiveTrueOrderByNameAsc().stream().map(mapper::toProductResponse).toList(); }
    public ProductResponse getProduct(Long id) { return mapper.toProductResponse(products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id))); }
    public List<CategoryResponse> listCategories() { return categories.findAll().stream().map(mapper::toCategoryResponse).toList(); }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (products.existsBySku(request.sku())) throw new BusinessRuleException("Product SKU already exists");
        Category category = categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
        Product product = new Product(request.sku(), request.name(), request.description(), category, request.supplierName(), request.unitPrice(), new ColdChainRequirement(request.minCelsius(), request.maxCelsius(), request.handlingNotes()));
        return mapper.toProductResponse(products.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Category category = categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
        product.update(request.name(), request.description(), category, request.supplierName(), request.unitPrice(), new ColdChainRequirement(request.minCelsius(), request.maxCelsius(), request.handlingNotes()));
        return mapper.toProductResponse(product);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.deactivate();
    }
}
