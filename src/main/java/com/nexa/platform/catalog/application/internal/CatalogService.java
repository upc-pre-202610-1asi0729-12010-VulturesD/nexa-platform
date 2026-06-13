package com.nexa.platform.catalog.application.internal;

import com.nexa.platform.catalog.application.dtos.*;
import com.nexa.platform.catalog.application.commands.*;
import com.nexa.platform.catalog.application.commandservices.CatalogCommandService;
import com.nexa.platform.catalog.application.queries.*;
import com.nexa.platform.catalog.application.queryservices.CatalogQueryService;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.infrastructure.persistence.jpa.*;
import com.nexa.platform.shared.domain.exceptions.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService implements CatalogCommandService, CatalogQueryService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final CatalogMapper mapper;

    public CatalogService(ProductRepository products, CategoryRepository categories, CatalogMapper mapper) {
        this.products = products; this.categories = categories; this.mapper = mapper;
    }

    public List<ProductResponse> listProducts() { return products.findByActiveTrueOrderByIdAsc().stream().map(mapper::toProductResponse).toList(); }
    public ProductResponse getProduct(Long id) { return mapper.toProductResponse(products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id))); }
    public List<CategoryResponse> listCategories() { return categories.findAll().stream().map(mapper::toCategoryResponse).toList(); }

    @Override
    public List<ProductResponse> handle(GetProductsQuery query) {
        return listProducts();
    }

    @Override
    public ProductResponse handle(GetProductByIdQuery query) {
        return getProduct(query.id());
    }

    @Override
    public List<CategoryResponse> handle(GetCatalogCategoriesQuery query) {
        return listCategories();
    }

    @Override
    @Transactional
    public ProductResponse handle(CreateProductCommand command) {
        ProductRequest request = new ProductRequest(command.sku(), command.name(), command.description(), command.categoryId(),
            command.supplierName(), command.unitPrice(), command.unit(), command.imageUrl(), command.minCelsius(),
            command.maxCelsius(), command.handlingNotes());
        return createProduct(request);
    }

    @Override
    @Transactional
    public ProductResponse handle(UpdateProductCommand command) {
        ProductRequest request = new ProductRequest(null, command.name(), command.description(), command.categoryId(),
            command.supplierName(), command.unitPrice(), command.unit(), command.imageUrl(), command.minCelsius(),
            command.maxCelsius(), command.handlingNotes());
        return updateProduct(command.id(), request);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (products.existsBySku(request.sku())) throw new BusinessRuleException("Product SKU already exists");
        Category category = categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
        Product product = new Product(request.sku(), request.name(), request.description(), category, request.supplierName(), request.unitPrice(), request.unit(), request.imageUrl(), new ColdChainRequirement(request.minCelsius(), request.maxCelsius(), request.handlingNotes()));
        return mapper.toProductResponse(products.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        Category category = categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));
        product.update(request.name(), request.description(), category, request.supplierName(), request.unitPrice(), request.unit(), request.imageUrl(), new ColdChainRequirement(request.minCelsius(), request.maxCelsius(), request.handlingNotes()));
        return mapper.toProductResponse(product);
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.deactivate();
    }
}
