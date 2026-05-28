package com.nexa.platform.sales.application.internal;

import com.nexa.platform.catalog.domain.model.Product;
import com.nexa.platform.catalog.infrastructure.persistence.jpa.ProductRepository;
import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.infrastructure.persistence.jpa.*;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesService {
    private final CustomerRepository customers;
    private final SalesOrderRepository orders;
    private final ProductRepository products;
    private final SalesMapper mapper;
    public SalesService(CustomerRepository customers, SalesOrderRepository orders, ProductRepository products, SalesMapper mapper) { this.customers = customers; this.orders = orders; this.products = products; this.mapper = mapper; }
    public List<CustomerResponse> listCustomers() { return customers.findAll().stream().map(mapper::toCustomerResponse).toList(); }
    public List<OrderResponse> listOrders() { return orders.findAll().stream().map(mapper::toOrderResponse).toList(); }
    public OrderResponse getOrder(Long id) { return mapper.toOrderResponse(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales order", id))); }
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
}
