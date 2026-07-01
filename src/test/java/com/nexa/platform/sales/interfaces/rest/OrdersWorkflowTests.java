package com.nexa.platform.sales.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrdersWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void confirmationRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/orders/1/confirmations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "paymentConfirmation": "PAY-001", "inventoryReservation": "RES-001" }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void pendingOrderCanBeUpdatedAndConfirmedButNotCancelledAfterward() throws Exception {
        long id = createPendingOrder();

        mockMvc.perform(put("/api/v1/orders/{id}", id)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerId": 1,
                      "items": [{ "productId": 1, "quantity": 2 }],
                      "priority": "urgent",
                      "notes": "Cold-chain priority order."
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.priority").value("urgent"))
            .andExpect(jsonPath("$.notes").value("Cold-chain priority order."))
            .andExpect(jsonPath("$.items[0].quantity").value(2));

        mockMvc.perform(post("/api/v1/orders/{id}/confirmations", id)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "paymentConfirmation": "PAY-001", "inventoryReservation": "RES-001" }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("confirmed"))
            .andExpect(jsonPath("$.paymentConfirmation").value("PAY-001"))
            .andExpect(jsonPath("$.inventoryReservation").value("RES-001"))
            .andExpect(jsonPath("$.confirmedAt").isNotEmpty());

        mockMvc.perform(post("/api/v1/orders/{id}/cancellations", id).header(TENANT_HEADER, 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Confirmed or paid orders cannot be cancelled."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void rejectedOrderCannotBeConfirmed() throws Exception {
        long id = createPendingOrder();

        mockMvc.perform(post("/api/v1/orders/{id}/rejections", id)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "rejectionReason": "Credit validation failed." }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("rejected"))
            .andExpect(jsonPath("$.rejectionReason").value("Credit validation failed."));

        mockMvc.perform(post("/api/v1/orders/{id}/confirmations", id)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "paymentConfirmation": "PAY-002", "inventoryReservation": "RES-002" }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Rejected orders cannot be confirmed."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void orderTimelineCombinesOrderDispatchInvoiceAndPaymentEvents() throws Exception {
        mockMvc.perform(get("/api/v1/orders/1/timeline").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.orderNumber").value("ORD-2026-0001"))
            .andExpect(jsonPath("$.events[?(@.source == 'order')]").isNotEmpty())
            .andExpect(jsonPath("$.events[?(@.source == 'dispatch')]").isNotEmpty())
            .andExpect(jsonPath("$.events[?(@.source == 'invoice')]").isNotEmpty())
            .andExpect(jsonPath("$.events[?(@.source == 'payment')]").isNotEmpty());
    }

    @Test
    void salesOrderCreationCreatesLogisticsDispatchForTracking() throws Exception {
        String authorization = authorization("sales@nexa.com");

        String body = mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerId": 1,
                      "items": [{ "productId": 1, "quantity": 2 }]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.backendId").isNumber())
            .andReturn()
            .getResponse()
            .getContentAsString();

        long orderId = objectMapper.readTree(body).path("backendId").asLong();
        mockMvc.perform(get("/api/v1/orders/{id}/tracking", orderId)
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId))
            .andExpect(jsonPath("$.status").value("ready_for_operations"));

        mockMvc.perform(get("/api/v1/orders/{id}/timeline", orderId)
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events[?(@.source == 'dispatch')]").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void orderReadsAndActionsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/orders/1").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/orders").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(post("/api/v1/orders/1/cancellations").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/orders")
                .header(TENANT_HEADER, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerId": 1,
                      "items": [{ "productId": 1, "quantity": 1 }]
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void buyerJwtReadsOnlyOwnOrdersAndCannotRunManagedActions() throws Exception {
        String authorization = authorization("buyer@nexa.com");

        mockMvc.perform(get("/api/v1/orders").header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].customerId", everyItem(is(1))));

        mockMvc.perform(get("/api/v1/orders/2").header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/orders/1/cancellations").header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isForbidden());
    }

    private long createPendingOrder() throws Exception {
        String body = mockMvc.perform(post("/api/v1/orders")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "customerId": 1,
                      "items": [{ "productId": 1, "quantity": 1 }]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.status").value("pending"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode response = objectMapper.readTree(body);
        return response.path("backendId").asLong();
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
