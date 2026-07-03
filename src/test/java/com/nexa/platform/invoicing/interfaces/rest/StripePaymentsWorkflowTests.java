package com.nexa.platform.invoicing.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StripePaymentsWorkflowTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void stripePreparationRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/payments/stripe/checkout-sessions")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void unconfiguredStripeFoundationDoesNotCreatePayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments/stripe/checkout-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":1,\"currency\":\"PEN\"}"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.configured").value(false))
            .andExpect(jsonPath("$.ready").value(false))
            .andExpect(jsonPath("$.status").value("checkout_session_not_created"));
    }

    @Test
    void unconfiguredWebhookReturnsServiceUnavailable() throws Exception {
        mockMvc.perform(post("/api/v1/payments/stripe/webhook")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.message").value("STRIPE_WEBHOOK_SECRET is not configured."));
    }
}
