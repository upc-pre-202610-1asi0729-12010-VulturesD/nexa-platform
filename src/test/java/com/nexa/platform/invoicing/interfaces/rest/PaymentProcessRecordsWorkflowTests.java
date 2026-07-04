package com.nexa.platform.invoicing.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentProcessRecordsWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void paymentProcessesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/payment-process-records"))
            .andExpect(status().isForbidden());
    }

    @Test
    void paymentProcessWorkflowMatchesAppsWebContract() throws Exception {
        String authorization = authorization("sales@nexa.com");
        mockMvc.perform(post("/api/v1/payment-process-records")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tenantId": 1,
                      "orderId": 1,
                      "clientAccountId": 1,
                      "subtotal": 1200.00,
                      "discount": 0.00,
                      "shipping": 80.00,
                      "igv": 230.40,
                      "total": 1510.40,
                      "status": "pending"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("pending"))
            .andExpect(jsonPath("$.total").value(1510.40));

        mockMvc.perform(post("/api/v1/payment-process-records/1/confirmations")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("confirmed"));

        mockMvc.perform(post("/api/v1/payment-process-records/1/rejections")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isBadRequest());
    }

    @Test
    void buyerCanReadOwnProcessButCannotChangeStatus() throws Exception {
        String authorization = authorization("buyer@nexa.com");
        mockMvc.perform(get("/api/v1/payment-process-records")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clientAccountId").value(1));

        mockMvc.perform(post("/api/v1/payment-process-records/1/confirmations")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isForbidden());
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
