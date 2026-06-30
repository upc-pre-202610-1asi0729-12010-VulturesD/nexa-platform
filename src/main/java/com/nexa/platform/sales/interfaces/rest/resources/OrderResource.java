package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResource(String id, Long backendId, Long tenantId, String orderNumber, Long customerId, String clientId, String customer,
                            String status, String priority, String date, String deliveryDate,
                            List<OrderItemResource> items, BigDecimal total, String notes,
                            String paymentConfirmation, String inventoryReservation, String rejectionReason,
                            OffsetDateTime confirmedAt, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
