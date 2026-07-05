package com.nexa.platform.logistics.application.dtos;

import java.time.OffsetDateTime;

public record UpsertDispatchOrderRequest(Long tenantId, Long orderId, Long clientAccountId, String code, String status,
                                         String routeName, String responsible, OffsetDateTime eta,
                                         String deliveryWindow) { }
