package com.nexa.platform.sales.interfaces.rest.resources;

public record CustomerResource(Long id, String businessName, String taxId, String contactEmail, String deliveryAddress) { }
