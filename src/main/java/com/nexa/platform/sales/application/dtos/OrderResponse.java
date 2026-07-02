package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(String id, Long backendId, Long tenantId, String orderNumber, Long customerId, String clientId, String customer,
                            String status, String priority, String date, String deliveryDate,
                            List<OrderItemResponse> items, BigDecimal total, String notes,
                            String paymentConfirmation, String inventoryReservation, String rejectionReason,
                            OffsetDateTime confirmedAt, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
