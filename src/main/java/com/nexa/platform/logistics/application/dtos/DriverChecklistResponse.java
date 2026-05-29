package com.nexa.platform.logistics.application.dtos;

public record DriverChecklistResponse(Long id, Long shipmentId, boolean vehicleClean, boolean temperatureRecorderReady, boolean sealsVerified, String notes) { }
