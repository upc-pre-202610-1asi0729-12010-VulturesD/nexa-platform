package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.dtos.StripePaymentDtos.StripePaymentPreparationRequest;
import com.nexa.platform.invoicing.application.dtos.StripePaymentDtos.StripePaymentPreparationResponse;
import com.nexa.platform.invoicing.application.internal.StripePaymentPreparationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@Tag(name = "Stripe Payment Foundation", description = "Non-charging Stripe preparation and signed webhook foundation")
@PreAuthorize("isAuthenticated()")
public class StripePaymentsController {
    private final StripePaymentPreparationService service;

    public StripePaymentsController(StripePaymentPreparationService service) {
        this.service = service;
    }

    @Operation(summary = "Prepare a Stripe Checkout Session without creating a charge")
    @PostMapping("/checkout-sessions")
    public ResponseEntity<StripePaymentPreparationResponse> checkout(
        @RequestBody StripePaymentPreparationRequest resource) {
        return preparation(service.prepareCheckoutSession(resource));
    }

    @Operation(summary = "Prepare a Stripe PaymentIntent without creating a charge")
    @PostMapping("/payment-intents")
    public ResponseEntity<StripePaymentPreparationResponse> paymentIntent(
        @RequestBody StripePaymentPreparationRequest resource) {
        return preparation(service.preparePaymentIntent(resource));
    }

    @Operation(summary = "Verify a Stripe webhook signature")
    @PostMapping("/webhook")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, Object>> webhook(
        @RequestBody(required = false) String payload,
        @RequestHeader(value = "Stripe-Signature", required = false) String signature) {
        if (!service.webhookConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "STRIPE_WEBHOOK_SECRET is not configured."));
        }
        if (!service.verifyWebhookSignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid Stripe webhook signature."));
        }
        return ResponseEntity.accepted().body(Map.of(
            "received", true,
            "processed", false,
            "message", "Stripe webhook signature verified. Event-specific payment state handling is pending."));
    }

    private ResponseEntity<StripePaymentPreparationResponse> preparation(StripePaymentPreparationResponse response) {
        return ResponseEntity.status(response.configured() ? HttpStatus.NOT_IMPLEMENTED : HttpStatus.SERVICE_UNAVAILABLE)
            .body(response);
    }
}
