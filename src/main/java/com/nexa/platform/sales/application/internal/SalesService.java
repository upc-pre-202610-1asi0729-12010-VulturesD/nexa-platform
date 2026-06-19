package com.nexa.platform.sales.application.internal;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.domain.model.repositories.ProductRepositoryPort;
import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.domain.model.repositories.*;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Comparator;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesService {
    private final CustomerRepositoryPort customers;
    private final SalesOrderRepositoryPort orders;
    private final ProductRepositoryPort products;
    private final SalesMapper mapper;
    public SalesService(CustomerRepositoryPort customers, SalesOrderRepositoryPort orders, ProductRepositoryPort products, SalesMapper mapper) { this.customers = customers; this.orders = orders; this.products = products; this.mapper = mapper; }
    @Transactional(readOnly = true)
    public List<CustomerResponse> listCustomers() { return customers.findAll().stream().map(mapper::toCustomerResponse).toList(); }
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return orders.findAll().stream()
            .sorted(Comparator.comparing(SalesOrder::getId))
            .map(mapper::toOrderResponse)
            .toList();
    }
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) { return mapper.toOrderResponse(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales order", id))); }
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String id) { return getOrder(orderId(id)); }
    public List<PurchaseRequestResponse> listPurchaseRequests() { return List.of(sourcePurchaseRequest(), sourceSecondPurchaseRequest()); }
    public PurchaseRequestResponse getPurchaseRequest(String id) {
        return listPurchaseRequests().stream()
            .filter(request -> request.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Purchase request", id));
    }
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customers.findById(request.customerId()).orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId()));
        SalesOrder order = new SalesOrder(customer);
        for (OrderItemRequest item : request.items()) {
            Product product = products.findById(item.productId()).orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));
            order.addItem(new SalesOrderItem(product, item.quantity(), product.getUnitPrice()));
        }
        return mapper.toOrderResponse(orders.save(order));
    }
    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        SalesOrder order = orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales order", id));
        order.changeStatus(OrderStatus.valueOf(request.status()));
        return mapper.toOrderResponse(order);
    }

    private PurchaseRequestResponse sourcePurchaseRequest() {
        return new PurchaseRequestResponse(
            "PR-2026-0001",
            "CLI-001",
            "submitted",
            "high",
            "2026-06-13",
            "ADDR-001",
            "invoice_guide_pod",
            "Prioritize chilled products for weekend demand.",
            "2026-06-10T09:10:00Z",
            List.of(
                new PurchaseRequestItemResponse("PROD-0004", "MORTADELLA BOLOGNA IGP CON PISTACCHIO MOLDE 7.5KG", 3, "box", new BigDecimal("690.00")),
                new PurchaseRequestItemResponse("PROD-0013", "QUESO EDAM BOLA MOLDE 1.9KG", 2, "box", new BigDecimal("112.10"))
            )
        );
    }

    private PurchaseRequestResponse sourceSecondPurchaseRequest() {
        return new PurchaseRequestResponse(
            "REQ-2026-0004",
            "CLI-001",
            "submitted",
            "normal",
            "2026-06-13",
            "ADDR-001",
            "invoice_guide_pod",
            "nose",
            "2026-06-10T04:36:46.906Z",
            List.of(new PurchaseRequestItemResponse("PROD-0001", "QUESO GRANA PADANO DOP 150G", 1, "UN", new BigDecimal("17.30")))
        );
    }

    private Long orderId(String id) {
        if (id != null && id.matches("ORD-2026-\\d{4}")) {
            return Long.parseLong(id.substring(id.lastIndexOf('-') + 1));
        }
        return Long.valueOf(id);
    }
}
