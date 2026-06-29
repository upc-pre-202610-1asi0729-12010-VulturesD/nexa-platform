package com.nexa.platform.catalog.application.internal;

import com.nexa.platform.catalog.application.dtos.*;
import com.nexa.platform.catalog.application.commands.*;
import com.nexa.platform.catalog.application.commandservices.CatalogCommandService;
import com.nexa.platform.catalog.application.queries.*;
import com.nexa.platform.catalog.application.queryservices.CatalogQueryService;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.domain.model.repositories.*;
import com.nexa.platform.shared.application.auditing.AuditLogService;
import com.nexa.platform.shared.domain.exceptions.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService implements CatalogCommandService, CatalogQueryService {
    private final ProductRepositoryPort products;
    private final CategoryRepositoryPort categories;
    private final BrandRepositoryPort brands;
    private final CatalogMapper mapper;
    private final AuditLogService auditLogs;

    public CatalogService(ProductRepositoryPort products, CategoryRepositoryPort categories,
                          BrandRepositoryPort brands, CatalogMapper mapper, AuditLogService auditLogs) {
        this.products = products;
        this.categories = categories;
        this.brands = brands;
        this.mapper = mapper;
        this.auditLogs = auditLogs;
    }

    public List<ProductResponse> listProducts() { return products.findByActiveTrueOrderByIdAsc().stream().map(mapper::toProductResponse).toList(); }
    public ProductResponse getProduct(Long id) { return mapper.toProductResponse(products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", id))); }
    public List<CategoryResponse> listCategories() { return categories.findAll().stream().map(mapper::toCategoryResponse).toList(); }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long id) {
        return mapper.toCategoryResponse(categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id)));
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name) {
        return mapper.toCategoryResponse(categories.findByNameIgnoreCase(name.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Category", name)));
    }

    @Transactional
    public CategoryResponse createCategory(Long tenantId, UpsertReferenceRequest request) {
        if (categories.existsByNameIgnoreCase(request.name().trim())) {
            throw new BusinessRuleException("Category name already exists.");
        }
        Category saved = categories.save(new Category(request.name(), request.description()));
        auditLogs.record(tenantId, "catalog.category_created", "Category", saved.getId(), null);
        return mapper.toCategoryResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long tenantId, Long id, UpsertReferenceRequest request) {
        Category category = categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categories.findByNameIgnoreCase(request.name().trim())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> { throw new BusinessRuleException("Category name already exists."); });
        category.update(request.name(), request.description());
        auditLogs.record(tenantId, "catalog.category_updated", "Category", id, null);
        return mapper.toCategoryResponse(category);
    }

    @Transactional
    public void deactivateCategory(Long tenantId, Long id) {
        Category category = categories.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.deactivate();
        auditLogs.record(tenantId, "catalog.category_deactivated", "Category", id, null);
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> listBrands() {
        return brands.findAllByOrderByNameAsc().stream().map(CatalogService::toBrandResponse).toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrand(Long id) {
        return toBrandResponse(brands.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", id)));
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrandByName(String name) {
        return toBrandResponse(brands.findByNameIgnoreCase(name.trim())
            .orElseThrow(() -> new ResourceNotFoundException("Brand", name)));
    }

    @Transactional
    public BrandResponse createBrand(Long tenantId, UpsertReferenceRequest request) {
        if (brands.existsByNameIgnoreCase(request.name().trim())) {
            throw new BusinessRuleException("Brand name already exists.");
        }
        Brand saved = brands.save(new Brand(request.name(), request.description()));
        auditLogs.record(tenantId, "catalog.brand_created", "Brand", saved.getId(), null);
        return toBrandResponse(saved);
    }

    @Transactional
    public BrandResponse updateBrand(Long tenantId, Long id, UpsertReferenceRequest request) {
        Brand brand = brands.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        brands.findByNameIgnoreCase(request.name().trim())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> { throw new BusinessRuleException("Brand name already exists."); });
        brand.update(request.name(), request.description());
        auditLogs.record(tenantId, "catalog.brand_updated", "Brand", id, null);
        return toBrandResponse(brand);
    }

    @Transactional
    public void deactivateBrand(Long tenantId, Long id) {
        Brand brand = brands.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        brand.deactivate();
        auditLogs.record(tenantId, "catalog.brand_deactivated", "Brand", id, null);
    }

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

    private static BrandResponse toBrandResponse(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName(), brand.getDescription(), brand.isActive());
    }
}
