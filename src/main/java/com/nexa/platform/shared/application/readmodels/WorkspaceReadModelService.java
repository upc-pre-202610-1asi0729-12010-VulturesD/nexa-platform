package com.nexa.platform.shared.application.readmodels;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.domain.model.repositories.ProductRepositoryPort;
import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;
import com.nexa.platform.shared.application.pagination.PagedResult;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceReadModelService {
    private final ProductRepositoryPort products;
    private final PromotionRepository promotions;

    public WorkspaceReadModelService(ProductRepositoryPort products, PromotionRepository promotions) {
        this.products = products;
        this.promotions = promotions;
    }

    @Transactional(readOnly = true)
    public PagedResult<PromotionalCatalogItemReadModel> promotionalCatalog(Integer requestedPage,
                                                                           Integer requestedPageSize) {
        int page = Math.max(requestedPage == null ? 1 : requestedPage, 1);
        int pageSize = Math.max(1, Math.min(requestedPageSize == null ? 25 : requestedPageSize, 100));
        List<Product> activeProducts = products.findByActiveTrueOrderByIdAsc().stream()
            .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
            .toList();
        List<Promotion> activePromotions = promotions.findAll().stream()
            .filter(promotion -> "active".equalsIgnoreCase(promotion.getStatus()))
            .toList();

        int totalItems = activeProducts.size();
        int totalPages = totalItems == 0 ? 0 : (int) Math.ceil((double) totalItems / pageSize);
        int from = Math.min((page - 1) * pageSize, totalItems);
        int to = Math.min(from + pageSize, totalItems);
        List<PromotionalCatalogItemReadModel> items = activeProducts.subList(from, to).stream()
            .map(product -> toPromotionalItem(product, activePromotions))
            .toList();
        return new PagedResult<>(items, page, pageSize, totalItems, totalPages);
    }

    private static PromotionalCatalogItemReadModel toPromotionalItem(Product product,
                                                                      List<Promotion> promotions) {
        Promotion activePromotion = promotions.stream()
            .filter(promotion -> promotion.getProductIds().contains(product.getSku()))
            .findFirst()
            .orElse(null);
        return new PromotionalCatalogItemReadModel(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getSupplierName(),
            product.getCategory().getName(),
            product.getUnitPrice(),
            "PEN",
            activePromotion == null ? null : activePromotion.getPromoCode(),
            activePromotion == null ? null : activePromotion.getDiscountLabel(),
            Math.max(0, product.getAvailableStock() - product.getReservedStock()));
    }
}
