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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationRecordsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void notificationsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")).andExpect(status().isForbidden());
    }

    @Test
    void buyerOnlyReadsAndMarksOwnNotifications() throws Exception {
        String salesToken = token("sales@nexa.com");
        String buyerToken = token("buyer@nexa.com");
        long buyerNotificationId = createNotification(salesToken, 1, "Order ready");
        long otherNotificationId = createNotification(salesToken, 2, "Other client update");

        mockMvc.perform(get("/api/v1/notifications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(buyerNotificationId))
            .andExpect(jsonPath("$[0].read").value(false));

        mockMvc.perform(get("/api/v1/notifications/{id}", otherNotificationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/notifications/{id}/reads", buyerNotificationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.read").value(true));

        mockMvc.perform(post("/api/v1/notifications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 1,
                      "title": "Unauthorized",
                      "body": "Buyer cannot create",
                      "recipientRole": "buyer",
                      "type": "status"
                    }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void notificationsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/notifications").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/notifications/1").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());
    }

    private long createNotification(String token, long clientAccountId, String title) throws Exception {
        String body = mockMvc.perform(post("/api/v1/notifications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": %d,
                      "recipientRole": "buyer",
                      "type": "status",
                      "title": "%s",
                      "body": "Cold-chain workflow updated.",
                      "read": false
                    }
                    """.formatted(clientAccountId, title)))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/notifications/\\d+")))
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
