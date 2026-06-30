package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record ConfirmOrderResource(@NotBlank String paymentConfirmation,
                                   @NotBlank String inventoryReservation) { }
