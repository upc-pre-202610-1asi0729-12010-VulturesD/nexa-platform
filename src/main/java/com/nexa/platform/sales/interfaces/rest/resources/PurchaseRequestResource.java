package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseRequestResource(
    Long id,
    Long backendId,
    Long requestId,
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
    List<PurchaseRequestItemResource> items
) { }
