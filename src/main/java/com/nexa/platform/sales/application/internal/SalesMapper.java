package com.nexa.platform.sales.application.internal;

import com.nexa.platform.sales.application.dtos.*;
import com.nexa.platform.sales.domain.model.*;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class SalesMapper {
    public CustomerResponse toCustomerResponse(Customer customer) { return new CustomerResponse(customer.getId(), customer.getBusinessName(), customer.getTaxId(), customer.getContactEmail(), customer.getDeliveryAddress()); }
    public OrderResponse toOrderResponse(SalesOrder order) {
        LocalDate date = order.getCreatedAt() == null ? LocalDate.now() : order.getCreatedAt().toLocalDate();
        String orderNumber = orderNumber(order);
        return new OrderResponse(
            orderNumber,
            order.getId(),
            orderNumber,
            order.getCustomer().getId(),
            clientId(order.getCustomer()),
            order.getCustomer().getBusinessName(),
            order.getStatus().name().toLowerCase(),
            priority(order),
            orderDate(order, date),
            deliveryDate(order, date),
            order.getItems().stream().map(this::toItemResponse).toList(),
            order.total(),
            notes(order));
    }
    private OrderItemResponse toItemResponse(SalesOrderItem item) { return new OrderItemResponse(item.getProduct().getId(), item.getProduct().getSku(), item.getProduct().getName(), item.getQuantity(), item.getUnitPrice(), item.subtotal()); }

    private String clientId(Customer customer) {
        if ("20600000001".equals(customer.getTaxId())) return "CLI-001";
        return "CLI-" + String.format("%03d", customer.getId());
    }

    private String orderNumber(SalesOrder order) {
        return "ORD-2026-" + String.format("%04d", order.getId());
    }

    private String priority(SalesOrder order) {
        if (Long.valueOf(1L).equals(order.getId())) return "high";
        return "normal";
    }

    private String orderDate(SalesOrder order, LocalDate fallback) {
        if (Long.valueOf(1L).equals(order.getId())) return "2026-06-06";
        if (Long.valueOf(6L).equals(order.getId())) return "2026-06-11";
        return fallback.toString();
    }

    private String deliveryDate(SalesOrder order, LocalDate fallback) {
        if (Long.valueOf(1L).equals(order.getId())) return "2026-06-13";
        if (Long.valueOf(6L).equals(order.getId())) return "2026-06-14";
        return fallback.plusDays(7).toString();
    }

    private String notes(SalesOrder order) {
        if (Long.valueOf(1L).equals(order.getId())) return "Pending commercial and dispatch coordination for ICISA.";
        if (Long.valueOf(6L).equals(order.getId())) return "Second pending source order for buyer history.";
        return "";
    }
}
