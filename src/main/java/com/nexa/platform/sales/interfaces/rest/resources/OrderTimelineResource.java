package com.nexa.platform.sales.interfaces.rest.resources;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderTimelineResource(Long orderId, String orderNumber, List<TimelineEventResource> events) {
    public record TimelineEventResource(String source, String status, String description, OffsetDateTime occurredAt) { }
}
