package com.nexa.platform.logistics.application.dtos;

public record ShipmentResponse(Long id, Long orderId, String route, String carrier, String status, String trackingNote) { }
