package com.nexa.platform.invoicing.application.internal;

import com.nexa.platform.invoicing.application.dtos.StripePaymentDtos.StripePaymentPreparationRequest;
import com.nexa.platform.invoicing.application.dtos.StripePaymentDtos.StripePaymentPreparationResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentPreparationService {
    private static final long SIGNATURE_TOLERANCE_SECONDS = 300;
    private final Environment environment;

    public StripePaymentPreparationService(Environment environment) {
        this.environment = environment;
    }

    public StripePaymentPreparationResponse prepareCheckoutSession(StripePaymentPreparationRequest request) {
        return result("checkout_session_not_created",
            "Stripe Checkout Sessions require server-side Stripe SDK integration. No payment was created or marked as paid.");
    }

    public StripePaymentPreparationResponse preparePaymentIntent(StripePaymentPreparationRequest request) {
        return result("payment_intent_not_created",
            "Stripe PaymentIntents require server-side Stripe SDK integration. No payment was created or marked as paid.");
    }

    public boolean webhookConfigured() { return !webhookSecret().isBlank(); }

    public boolean verifyWebhookSignature(String payload, String signatureHeader) {
        if (!webhookConfigured() || payload == null || signatureHeader == null || signatureHeader.isBlank()) return false;
        String timestamp = null;
        java.util.List<String> signatures = new java.util.ArrayList<>();
        for (String part : signatureHeader.split(",")) {
            String[] pair = part.trim().split("=", 2);
            if (pair.length != 2) continue;
            if ("t".equals(pair[0])) timestamp = pair[1];
            if ("v1".equals(pair[0])) signatures.add(pair[1]);
        }
        if (timestamp == null || signatures.isEmpty()) return false;
        long signedAt;
        try { signedAt = Long.parseLong(timestamp); }
        catch (NumberFormatException exception) { return false; }
        if (Math.abs(Instant.now().getEpochSecond() - signedAt) > SIGNATURE_TOLERANCE_SECONDS) return false;
        byte[] expected = hmac(webhookSecret(), timestamp + "." + payload).getBytes(StandardCharsets.UTF_8);
        return signatures.stream().anyMatch(signature ->
            java.security.MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.UTF_8)));
    }

    private StripePaymentPreparationResponse result(String status, String configuredMessage) {
        boolean configured = !secretKey().isBlank();
        return new StripePaymentPreparationResponse(configured, false, status,
            configured ? configuredMessage : "STRIPE_SECRET_KEY is not configured. No payment was created or marked as paid.",
            null, null);
    }

    private String secretKey() {
        return value("STRIPE_SECRET_KEY", "stripe.secret-key");
    }

    private String webhookSecret() {
        return value("STRIPE_WEBHOOK_SECRET", "stripe.webhook-secret");
    }

    private String value(String environmentName, String propertyName) {
        String value = environment.getProperty(environmentName);
        if (value == null || value.isBlank()) value = environment.getProperty(propertyName, "");
        return value == null ? "" : value.trim();
    }

    private static String hmac(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException exception) {
            throw new IllegalStateException("Stripe webhook signature could not be verified.", exception);
        }
    }
}
