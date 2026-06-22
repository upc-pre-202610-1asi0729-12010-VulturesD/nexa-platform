package com.nexa.platform.shared.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class AuditLogsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void auditLogsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")).andExpect(status().isForbidden());
    }

    @Test
    void criticalActionsCreateTenantScopedAuditRecordsAndBuyersSeeNone() throws Exception {
        String salesToken = token("sales@nexa.com");
        String buyerToken = token("buyer@nexa.com");
        String created = mockMvc.perform(post("/api/v1/credit-requests")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 1,
                      "code": "CRQ-AUDIT-001",
                      "requestedAmount": 8000.00,
                      "reason": "Audit coverage"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long requestId = objectMapper.readTree(created).path("id").asLong();

        mockMvc.perform(post("/api/v1/credit-requests/{id}/resolutions", requestId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"approved\",\"reviewedBy\":\"Sales Lead\",\"note\":\"Approved\"}"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/audit-logs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].action").value("credit_request.resolved"))
            .andExpect(jsonPath("$[0].tenantId").value(1))
            .andExpect(jsonPath("$[0].workspaceId").value(1))
            .andExpect(jsonPath("$[0].actorUserId").isNumber())
            .andExpect(jsonPath("$[1].action").value("credit_request.created"));

        mockMvc.perform(get("/api/v1/audit-logs?limit=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + salesToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/audit-logs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void auditLogsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/audit-logs/1").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());
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
