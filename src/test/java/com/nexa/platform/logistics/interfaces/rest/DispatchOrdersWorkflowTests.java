package com.nexa.platform.logistics.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class DispatchOrdersWorkflowTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void dispatchOrdersRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/dispatch-orders"))
            .andExpect(status().isForbidden());
    }

    @Test
    void salesCanTrackDispatchesButCannotMutateLifecycle() throws Exception {
        String token = login("sales@nexa.com");

        mockMvc.perform(get("/api/v1/dispatch-orders")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/dispatch-orders/1/assignees")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "responsible": "Unauthorized assignment" }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void dispatchOrdersRejectWrongTenantHeaderFromRealJwt() throws Exception {
        String token = login("logistics@nexa.com");

        mockMvc.perform(get("/api/v1/dispatch-orders")
                .header("Authorization", "Bearer " + token)
                .header("X-Nexa-Tenant-Id", "2"))
            .andExpect(status().isForbidden());
    }

    @Test
    void orderDispatchAliasUsesWorkspaceTenantAndOrderClient() throws Exception {
        String token = login("logistics@nexa.com");

        mockMvc.perform(post("/api/v1/orders/1/dispatch-orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "code": "DSP-ORD-ALIAS-9001", "routeName": "Workspace scoped cold route" }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.clientAccountId").value(1))
            .andExpect(jsonPath("$.code").value("DSP-ORD-ALIAS-9001"));
    }

    private String login(String email) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()
            .replaceAll("^.*\\\"token\\\":\\\"([^\\\"]+)\\\".*$", "$1");
    }

    @Test
    @WithMockUser(roles = "LOGISTICS")
    void dispatchOrderLifecycleMatchesAppsWebContract() throws Exception {
        mockMvc.perform(get("/api/v1/dispatch-orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("DSP-BUY-2026-0301"))
            .andExpect(jsonPath("$.status").value("ready_for_operations"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/assignees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "responsible": "Roberto Garcia" }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("assigned"))
            .andExpect(jsonPath("$.responsible").value("Roberto Garcia"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "eta": "2026-06-13T14:00:00-05:00", "deliveryWindow": "14:00-18:00", "note": "Route confirmed." }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("scheduled"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/route-starts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("in_route"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/deliveries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("delivered"));

        mockMvc.perform(get("/api/v1/dispatch-orders/1/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.status == 'assigned')]").isNotEmpty())
            .andExpect(jsonPath("$[?(@.status == 'scheduled')]").isNotEmpty())
            .andExpect(jsonPath("$[?(@.status == 'in_route')]").isNotEmpty())
            .andExpect(jsonPath("$[?(@.status == 'delivered')]").isNotEmpty());
    }
}
