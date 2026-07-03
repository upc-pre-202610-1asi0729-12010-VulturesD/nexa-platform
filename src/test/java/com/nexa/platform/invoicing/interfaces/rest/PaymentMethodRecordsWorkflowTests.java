package com.nexa.platform.invoicing.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentMethodRecordsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void paymentMethodsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/payment-method-records").header(TENANT_HEADER, "1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void paymentMethodDefaultAndStatusRulesMatchAppsWebContract() throws Exception {
        mockMvc.perform(get("/api/v1/payment-method-records").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("credit_line"))
            .andExpect(jsonPath("$[0].isDefault").value(true));

        String createdResponse = mockMvc.perform(post("/api/v1/payment-method-records")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 1,
                      "type": "transfer",
                      "label": "ICISA bank transfer",
                      "isDefault": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/payment-method-records/\\d+")))
            .andExpect(jsonPath("$.status").value("active"))
            .andExpect(jsonPath("$.isDefault").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long createdId = objectMapper.readTree(createdResponse).get("id").asLong();
        mockMvc.perform(get("/api/v1/payment-method-records").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(createdId))
            .andExpect(jsonPath("$[0].isDefault").value(true))
            .andExpect(jsonPath("$[1].isDefault").value(false));

        mockMvc.perform(post("/api/v1/payment-method-records/{id}/status-changes", createdId)
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "status": "disabled", "isDefault": true }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("disabled"))
            .andExpect(jsonPath("$.isDefault").value(false));

        mockMvc.perform(get("/api/v1/payment-method-records/{id}", createdId)
                .header(TENANT_HEADER, "2"))
            .andExpect(status().isNotFound());
    }

    @Test
    void buyerJwtOnlyReadsAndCreatesMethodsForOwnClient() throws Exception {
        String authorization = authorization("buyer@nexa.com");
        mockMvc.perform(get("/api/v1/payment-method-records")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clientAccountId").value(1));

        mockMvc.perform(post("/api/v1/payment-method-records")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientAccountId\":2,\"type\":\"card\",\"label\":\"Other client\",\"isDefault\":false}"))
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
