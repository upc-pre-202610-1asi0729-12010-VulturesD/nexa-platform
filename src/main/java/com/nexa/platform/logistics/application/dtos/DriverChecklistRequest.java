package com.nexa.platform.logistics.application.dtos;

import jakarta.validation.constraints.NotNull;

public record DriverChecklistRequest(@NotNull Long shipmentId, boolean vehicleClean, boolean temperatureRecorderReady, boolean sealsVerified, String notes) { }
