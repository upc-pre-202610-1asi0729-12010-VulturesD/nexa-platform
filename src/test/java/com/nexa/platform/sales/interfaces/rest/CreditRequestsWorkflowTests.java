package com.nexa.platform.sales.interfaces.rest;

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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CreditRequestsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creditRequestsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/credit-requests")).andExpect(status().isForbidden());
    }

    @Test
    void buyerCreatesOnlyForOwnClientAndSalesResolves() throws Exception {
        String buyerToken = token("buyer@nexa.com");
        String salesToken = token("sales@nexa.com");

        mockMvc.perform(post("/api/v1/credit-requests")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 2,
                      "requestedAmount": 5000.00,
                      "reason": "Invalid cross-client request"
                    }
                    """))
            .andExpect(status().isForbidden());

        long buyerRequestId = createRequest(buyerToken, null, "CRQ-BUYER-001", "7500.00");
        createRequest(salesToken, 2L, "CRQ-SALES-002", "12000.00");

        mockMvc.perform(get("/api/v1/credit-requests")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].clientAccountId").value(1));

        mockMvc.perform(post("/api/v1/credit-requests/{id}/resolutions", buyerRequestId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"approved\",\"reviewedBy\":\"Buyer\",\"note\":\"Not allowed\"}"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/credit-requests/{id}/resolutions", buyerRequestId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"approved\",\"reviewedBy\":\"Sales Lead\",\"note\":\"Credit validated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("approved"))
            .andExpect(jsonPath("$.reviewedBy").value("Sales Lead"))
            .andExpect(jsonPath("$.resolutionNote").value("Credit validated"));

        mockMvc.perform(post("/api/v1/credit-requests/{id}/resolutions", buyerRequestId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"rejected\",\"reviewedBy\":\"Sales Lead\",\"note\":\"Repeated\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Only submitted credit requests can be resolved."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void creditRequestsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/credit-requests").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/credit-requests/1").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());
    }

    private long createRequest(String token, Long clientAccountId, String code, String amount) throws Exception {
        String clientField = clientAccountId == null ? "" : "\"clientAccountId\":" + clientAccountId + ",";
        String body = mockMvc.perform(post("/api/v1/credit-requests")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      %s
                      "code": "%s",
                      "requestedAmount": %s,
                      "reason": "Seasonal cold-chain purchase capacity"
                    }
                    """.formatted(clientField, code, amount)))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/credit-requests/\\d+")))
            .andExpect(jsonPath("$.status").value("submitted"))
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).path("id").asLong();
    }

    private String token(String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("token").asText();
    }
}
