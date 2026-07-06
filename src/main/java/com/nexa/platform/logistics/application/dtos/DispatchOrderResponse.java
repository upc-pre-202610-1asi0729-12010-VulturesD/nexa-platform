package com.nexa.platform.logistics.application.dtos;

import java.time.OffsetDateTime;

public record DispatchOrderResponse(Long id, Long tenantId, Long orderId, Long clientAccountId, String code,
                                    String status, String routeName, String responsible, OffsetDateTime eta,
                                    String deliveryWindow, OffsetDateTime createdAt, OffsetDateTime updatedAt) { }
