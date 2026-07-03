package com.nexa.platform.invoicing.application.dtos;

import java.math.BigDecimal;

public final class StripePaymentDtos {
    private StripePaymentDtos() { }

    public record StripePaymentPreparationRequest(Long paymentId, Long invoiceId, Long orderId,
                                                   BigDecimal amount, String currency,
                                                   String successUrl, String cancelUrl) { }
    public record StripePaymentPreparationResponse(boolean configured, boolean ready, String status,
                                                    String message, String checkoutUrl, String clientSecret) { }
}
