package com.nexa.platform.sales.application.internal;

import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class SalesMapper {
    public CustomerResponse toCustomerResponse(Customer customer) { return new CustomerResponse(customer.getId(), customer.getBusinessName(), customer.getTaxId(), customer.getContactEmail(), customer.getDeliveryAddress()); }
    public OrderResponse toOrderResponse(SalesOrder order) {
        return new OrderResponse(order.getId(), order.getCustomer().getBusinessName(), order.getStatus().name(), order.getItems().stream().map(this::toItemResponse).toList(), order.total());
    }
    private OrderItemResponse toItemResponse(SalesOrderItem item) { return new OrderItemResponse(item.getProduct().getId(), item.getProduct().getSku(), item.getProduct().getName(), item.getQuantity(), item.getUnitPrice(), item.subtotal()); }
}
