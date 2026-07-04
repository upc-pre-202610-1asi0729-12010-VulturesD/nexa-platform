package com.nexa.platform.invoicing.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void paymentsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/payments").header(TENANT_HEADER, 1))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void pendingPaymentCanBeUpdatedAndConfirmedButNotCancelledOrRejectedAfterward() throws Exception {
        long paymentId = createPayment("PAY-TEST-CONFIRM", "100.00");

        mockMvc.perform(patch("/api/v1/payments/{id}", paymentId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceId": 2,
                      "clientAccountId": 1,
                      "amount": 1600.00,
                      "currency": "PEN",
                      "method": "bank_transfer",
                      "referenceCode": "PAY-TEST-CONFIRM-UPDATED"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.referenceCode").value("PAY-TEST-CONFIRM-UPDATED"))
            .andExpect(jsonPath("$.amount").value(1600.00))
            .andExpect(jsonPath("$.status").value("pending"));

        mockMvc.perform(post("/api/v1/payments/{id}/confirmations", paymentId)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("confirmed"))
            .andExpect(jsonPath("$.confirmedAt").isNotEmpty());

        mockMvc.perform(post("/api/v1/payments/{id}/cancellations", paymentId)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Confirmed payments cannot be cancelled."));

        mockMvc.perform(post("/api/v1/payments/{id}/rejections", paymentId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"reason\": \"Late rejection\" }"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Confirmed payments cannot be rejected."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void rejectedPaymentCannotBeConfirmed() throws Exception {
        long paymentId = createPayment("PAY-TEST-REJECT", "90.00");

        mockMvc.perform(post("/api/v1/payments/{id}/rejections", paymentId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"reason\": \"Bank validation failed.\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("rejected"))
            .andExpect(jsonPath("$.rejectionReason").value("Bank validation failed."))
            .andExpect(jsonPath("$.rejectedAt").isNotEmpty());

        mockMvc.perform(post("/api/v1/payments/{id}/confirmations", paymentId)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Only pending payments can be confirmed."));
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void invoicePaymentSubresourceListsTenantScopedPayments() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/1/payments").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].invoiceId").value(1))
            .andExpect(jsonPath("$[0].tenantId").value(1));
    }

    @Test
    void buyerJwtReadsOwnPaymentsButCannotRegisterPayment() throws Exception {
        String authorization = authorization("buyer@nexa.com");
        mockMvc.perform(get("/api/v1/payments")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clientAccountId").value(1));

        mockMvc.perform(post("/api/v1/payments")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"invoiceId\":1,\"clientAccountId\":1,\"amount\":10,\"currency\":\"PEN\",\"method\":\"card\",\"referenceCode\":\"BUYER-WRITE\"}"))
            .andExpect(status().isForbidden());
    }

    private long createPayment(String reference, String amount) throws Exception {
        String body = mockMvc.perform(post("/api/v1/payments")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceId": 2,
                      "clientAccountId": 1,
                      "amount": %s,
                      "currency": "PEN",
                      "method": "bank_transfer",
                      "referenceCode": "%s"
                    }
                    """.formatted(amount, reference)))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/payments/\\d+")))
            .andExpect(jsonPath("$.status").value("pending"))
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).path("backendId").asLong();
    }

    private String authorization(String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return "Bearer " + objectMapper.readTree(response).path("token").asText();
    }
}
