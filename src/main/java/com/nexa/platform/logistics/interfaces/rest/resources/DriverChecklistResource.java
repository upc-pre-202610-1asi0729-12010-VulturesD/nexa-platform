package com.nexa.platform.logistics.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record DriverChecklistResource(Long id, @NotNull Long shipmentId, boolean vehicleClean,
                                      boolean temperatureRecorderReady, boolean sealsVerified, String notes) { }
