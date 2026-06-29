package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseRequestResponse(
    Long id,
    Long tenantId,
    Long clientAccountId,
    String code,
    String origin,
    String status,
    String priority,
    String requestedDeliveryDate,
    String deliveryAddress,
    String deliveryDistrict,
    String deliveryCity,
    String deliveryProvince,
    String deliveryReference,
    String paymentOption,
    BigDecimal shippingEstimate,
    String comments,
    String commercialOwner,
    String createdAt,
    String updatedAt,
    String clientId,
    String deliveryAddressId,
    String documentProfile,
    List<PurchaseRequestItemResponse> items
) { }
