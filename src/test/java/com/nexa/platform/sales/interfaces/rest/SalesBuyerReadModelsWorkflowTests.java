package com.nexa.platform.sales.interfaces.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SalesBuyerReadModelsWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void salesReadModelsArePagedTenantScopedAndRoleProtected() throws Exception {
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/sales/order-summaries")
                .queryParam("page", "1")
                .queryParam("pageSize", "2")
                .header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.pageSize").value(2))
            .andExpect(jsonPath("$.totalItems", greaterThan(2)))
            .andExpect(jsonPath("$.items[0].orderNumber").exists())
            .andExpect(jsonPath("$.items[0].client.code").exists())
            .andExpect(jsonPath("$.items[0].currency").value("PEN"));

        mockMvc.perform(get("/api/v1/sales/purchase-request-inbox")
                .header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.pageSize").value(25));

        mockMvc.perform(get("/api/v1/sales/order-summaries")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isForbidden());
    }

    @Test
    void buyerDashboardAndFinancialProfileUseAuthenticatedClientScope() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/buyer/dashboard-summary")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activeOrdersCount", greaterThan(0)))
            .andExpect(jsonPath("$.recentOrders").isArray())
            .andExpect(jsonPath("$.creditSummary.estimated").value(false));

        mockMvc.perform(get("/api/v1/buyer/financial-profile")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.client.id").value(1))
            .andExpect(jsonPath("$.client.code").value("CLI-001"))
            .andExpect(jsonPath("$.credit.availableCredit").isNumber())
            .andExpect(jsonPath("$.paymentMethodsCount", greaterThan(0)));

        mockMvc.perform(get("/api/v1/buyer/dashboard-summary")
                .header("Authorization", bearer(salesToken)))
            .andExpect(status().isForbidden());
    }

    @Test
    void buyerLifecycleIncludesOwnedCrossContextDataAndHidesOtherClientOrders() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/buyer/orders/1/lifecycle")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order.id").value(1))
            .andExpect(jsonPath("$.order.client.id").value(1))
            .andExpect(jsonPath("$.order.client.code").value("CLI-001"))
            .andExpect(jsonPath("$.items.length()", greaterThan(0)))
            .andExpect(jsonPath("$.dispatches").isArray())
            .andExpect(jsonPath("$.businessDocuments").isArray())
            .andExpect(jsonPath("$.invoices").isArray())
            .andExpect(jsonPath("$.payments").isArray());

        mockMvc.perform(get("/api/v1/buyer/orders/2/lifecycle")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isNotFound());
    }

    @Test
    void buyerPortalCollectionsEnforceClientOwnershipAndMessageVisibility() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");

        String hiddenMessage = mockMvc.perform(post("/api/v1/conversation-messages")
                .header("Authorization", bearer(salesToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 1,
                      "purchaseRequestId": 1,
                      "senderRole": "commercial",
                      "senderName": "Sales private note",
                      "body": "Internal validation note",
                      "visibleToBuyer": false
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long hiddenMessageId = objectMapper.readTree(hiddenMessage).path("id").asLong();

        mockMvc.perform(get("/api/v1/purchase-requests")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].clientId", everyItem(is("CLI-001"))));

        mockMvc.perform(get("/api/v1/conversation-messages")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].clientAccountId", everyItem(is(1))))
            .andExpect(jsonPath("$[*].visibleToBuyer", everyItem(is(true))));

        mockMvc.perform(get("/api/v1/conversation-messages/{id}", hiddenMessageId)
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/conversation-messages")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientAccountId": 2,
                      "purchaseRequestId": 1,
                      "senderRole": "commercial",
                      "senderName": "Buyer contact",
                      "body": "Please confirm the delivery window",
                      "visibleToBuyer": false
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clientAccountId").value(1))
            .andExpect(jsonPath("$.senderRole").value("buyer"))
            .andExpect(jsonPath("$.visibleToBuyer").value(true));

        mockMvc.perform(put("/api/v1/purchase-requests/1")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientAccountId\":2}"))
            .andExpect(status().isNotFound());
    }

    private String login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("token").asText();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
