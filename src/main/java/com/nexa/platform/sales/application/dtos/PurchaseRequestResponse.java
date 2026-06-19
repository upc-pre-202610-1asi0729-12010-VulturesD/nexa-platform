package com.nexa.platform.sales.application.dtos;

import java.util.List;

public record PurchaseRequestResponse(String id, String clientId, String status, String priority,
                                      String requestedDeliveryDate, String deliveryAddressId,
                                      String documentProfile, String comments, String createdAt,
                                      List<PurchaseRequestItemResponse> items) { }
