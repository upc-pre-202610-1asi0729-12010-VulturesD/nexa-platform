package com.nexa.platform.sales.interfaces.rest.resources;

import java.util.List;

public record PurchaseRequestResource(String id, String clientId, String status, String priority,
                                      String requestedDeliveryDate, String deliveryAddressId,
                                      String documentProfile, String comments, String createdAt,
                                      List<PurchaseRequestItemResource> items) { }
