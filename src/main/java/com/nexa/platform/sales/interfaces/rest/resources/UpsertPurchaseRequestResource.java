package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpsertPurchaseRequestResource(
    Long tenantId,
    @Positive Long clientAccountId,
    @Size(max = 40) String clientId,
    @Size(max = 40) String code,
    @Size(max = 40) String origin,
    @Size(max = 40) String status,
    @Size(max = 40) String priority,
    LocalDate requestedDeliveryDate,
    @Size(max = 160) String deliveryAddress,
    @Size(max = 80) String deliveryDistrict,
    @Size(max = 80) String deliveryCity,
    @Size(max = 80) String deliveryProvince,
    @Size(max = 180) String deliveryReference,
    @Size(max = 40) String paymentOption,
    BigDecimal shippingEstimate,
    @Size(max = 800) String comments,
    @Size(max = 120) String commercialOwner,
    @Valid List<UpsertPurchaseRequestItemResource> items
) { }
