package com.nexa.platform.sales.application.dtos;

public record CustomerResponse(Long id, String businessName, String taxId, String contactEmail, String deliveryAddress) { }
