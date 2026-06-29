package com.nexa.platform.sales.application.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderTimelineResponse(Long orderId, String orderNumber, List<TimelineEventResponse> events) {
    public record TimelineEventResponse(String source, String status, String description, OffsetDateTime occurredAt) { }
}
