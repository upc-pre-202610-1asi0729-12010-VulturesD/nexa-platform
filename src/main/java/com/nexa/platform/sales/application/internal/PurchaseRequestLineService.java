package com.nexa.platform.sales.application.internal;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.domain.model.repositories.ProductRepositoryPort;
import com.nexa.platform.sales.domain.model.Customer;
import com.nexa.platform.sales.domain.model.PurchaseRequest;
import com.nexa.platform.sales.domain.model.PurchaseRequestLine;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.PurchaseRequestRepositoryPort;
import com.nexa.platform.sales.interfaces.rest.resources.PurchaseRequestLineResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertPurchaseRequestLineResource;
import com.nexa.platform.shared.application.security.WorkspaceScopeException;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseRequestLineService {
    private final PurchaseRequestRepositoryPort requests;
    private final ProductRepositoryPort products;
    private final CustomerRepositoryPort customers;
    private final EntityManager entityManager;

    public PurchaseRequestLineService(PurchaseRequestRepositoryPort requests, ProductRepositoryPort products,
                                      CustomerRepositoryPort customers, EntityManager entityManager) {
        this.requests = requests;
        this.products = products;
        this.customers = customers;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestLineResource> list(Long tenantId, Long clientAccountId) {
        return requests.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(request -> canAccess(tenantId, clientAccountId, request))
            .flatMap(request -> request.getItems().stream().map(line -> toResource(request, line)))
            .toList();
    }

    @Transactional(readOnly = true)
    public PurchaseRequestLineResource get(Long tenantId, Long clientAccountId, Long id) {
        FoundLine found = findLine(tenantId, clientAccountId, id);
        return toResource(found.request(), found.line());
    }

    @Transactional
    public PurchaseRequestLineResource create(Long tenantId, Long clientAccountId,
                                              UpsertPurchaseRequestLineResource resource) {
        PurchaseRequest request = requestForWrite(tenantId, clientAccountId, resource.purchaseRequestId());
        Product product = product(resource.catalogItemId());
        int quantity = normalizedQuantity(resource.quantity());
        PurchaseRequestLine line = new PurchaseRequestLine(tenantId, product, quantity, unit(resource.unit(), product),
            nonNegative(resource.estimatedWeightKg(), "Estimated weight cannot be negative."), resource.notes());
        request.addItem(line);
        PurchaseRequest saved = requests.save(request);
        entityManager.flush();
        return toResource(saved, saved.getItems().get(saved.getItems().size() - 1));
    }

    @Transactional
    public PurchaseRequestLineResource update(Long tenantId, Long clientAccountId, Long id,
                                              UpsertPurchaseRequestLineResource resource) {
        FoundLine found = findLine(tenantId, clientAccountId, id);
        PurchaseRequest targetRequest = requestForWrite(tenantId, clientAccountId, resource.purchaseRequestId());
        if (!found.request().getId().equals(targetRequest.getId())) {
            found.request().getItems().remove(found.line());
            targetRequest.addItem(found.line());
        }
        Product product = product(resource.catalogItemId());
        found.line().update(tenantId, product, normalizedQuantity(resource.quantity()), unit(resource.unit(), product),
            nonNegative(resource.estimatedWeightKg(), "Estimated weight cannot be negative."), resource.notes());
        requests.save(targetRequest);
        entityManager.flush();
        return toResource(targetRequest, found.line());
    }

    @Transactional
    public void delete(Long tenantId, Long clientAccountId, Long id) {
        FoundLine found = findLine(tenantId, clientAccountId, id);
        found.request().getItems().remove(found.line());
        requests.save(found.request());
        entityManager.flush();
    }

    private PurchaseRequest requestForWrite(Long tenantId, Long clientAccountId, Long requestId) {
        PurchaseRequest request = requests.findByIdAndTenantId(requestId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request", requestId));
        if (!canAccess(tenantId, clientAccountId, request)) {
            throw new WorkspaceScopeException("Purchase request does not belong to the authenticated buyer.");
        }
        return request;
    }

    private FoundLine findLine(Long tenantId, Long clientAccountId, Long id) {
        return requests.findByTenantIdOrderByIdAsc(tenantId).stream()
            .filter(request -> canAccess(tenantId, clientAccountId, request))
            .flatMap(request -> request.getItems().stream()
                .filter(line -> id.equals(line.getId()))
                .map(line -> new FoundLine(request, line)))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request line", id));
    }

    private boolean canAccess(Long tenantId, Long clientAccountId, PurchaseRequest request) {
        if (clientAccountId == null) return true;
        return customers.findByIdAndTenantId(clientAccountId, tenantId)
            .map(Customer::getCode)
            .map(code -> code.equalsIgnoreCase(request.getClientId()))
            .orElse(false);
    }

    private Product product(Long id) {
        return products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Catalog item", id));
    }

    private static int normalizedQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) throw new IllegalArgumentException("Quantity must be positive.");
        try {
            return quantity.intValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Quantity must be a whole number.");
        }
    }

    private static BigDecimal nonNegative(BigDecimal value, String message) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value;
        if (normalized.signum() < 0) throw new IllegalArgumentException(message);
        return normalized;
    }

    private static String unit(String unit, Product product) {
        return unit == null || unit.isBlank() ? product.getUnit() : unit.trim();
    }

    private static PurchaseRequestLineResource toResource(PurchaseRequest request, PurchaseRequestLine line) {
        return new PurchaseRequestLineResource(line.getId(), line.getTenantId(), request.getId(),
            line.getProduct().getId(), BigDecimal.valueOf(line.getQuantity()), line.getUnit(),
            line.getEstimatedWeightKg(), line.getNotes(), line.getCreatedAt(), line.getUpdatedAt());
    }

    private record FoundLine(PurchaseRequest request, PurchaseRequestLine line) { }
}
