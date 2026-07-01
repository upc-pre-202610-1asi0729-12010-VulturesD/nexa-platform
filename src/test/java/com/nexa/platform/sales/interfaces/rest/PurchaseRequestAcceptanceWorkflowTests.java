package com.nexa.platform.sales.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PurchaseRequestAcceptanceWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void acceptanceRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests/REQ-2026-0004/acceptances")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"note\": \"Accept\" }"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void acceptanceCreatesOrderReservationDocumentsDispatchAndMessageIdempotently() throws Exception {
        validateCommercially("REQ-2026-0004");

        String body = mockMvc.perform(post("/api/v1/purchase-requests/REQ-2026-0004/acceptances")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"note\": \"Approved for cold-chain fulfillment.\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.purchaseRequestId").isNumber())
            .andExpect(jsonPath("$.orderId").isNumber())
            .andExpect(jsonPath("$.dispatchOrderId").isNumber())
            .andExpect(jsonPath("$.status").value("accepted"))
            .andReturn().getResponse().getContentAsString();

        JsonNode accepted = objectMapper.readTree(body);
        long orderId = accepted.path("orderId").asLong();
        long dispatchOrderId = accepted.path("dispatchOrderId").asLong();

        mockMvc.perform(get("/api/v1/orders/{id}", orderId).header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.status").value("pending"))
            .andExpect(jsonPath("$.notes").value("Approved for cold-chain fulfillment."));

        mockMvc.perform(get("/api/v1/orders/{id}/timeline", orderId).header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events[?(@.source == 'dispatch')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/reservations").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.orderId == 'ORD-2026-" + String.format("%04d", orderId) + "')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/business-documents").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.orderId == " + orderId + ")]").isNotEmpty());

        mockMvc.perform(get("/api/v1/dispatch-orders/{id}", dispatchOrderId).header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId))
            .andExpect(jsonPath("$.status").value("ready_for_operations"));

        mockMvc.perform(get("/api/v1/conversation-messages").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.orderId == " + orderId + ")]").isNotEmpty());

        mockMvc.perform(post("/api/v1/purchase-requests/REQ-2026-0004/acceptances")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"note\": \"Repeated action\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId))
            .andExpect(jsonPath("$.dispatchOrderId").value(dispatchOrderId))
            .andExpect(jsonPath("$.status").value("already_accepted"));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void commerciallyValidatedRequestCanCreateExplicitReservation() throws Exception {
        validateCommercially("REQ-2026-0004");

        mockMvc.perform(post("/api/v1/purchase-requests/REQ-2026-0004/reservations")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "RES-PR-MANUAL-001",
                      "productId": "PROD-0001",
                      "lotCode": "LOT-ICISA-001",
                      "units": 2
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.externalId").value("RES-PR-MANUAL-001"))
            .andExpect(jsonPath("$.status").value("reserved"));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void purchaseRequestMessageAliasPersistsTenantScopedConversation() throws Exception {
        String body = mockMvc.perform(post("/api/v1/purchase-requests/REQ-2026-0004/messages")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "body": "Delivery window confirmed.",
                      "senderRole": "commercial",
                      "senderName": "Sales Lead",
                      "visibleToBuyer": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.purchaseRequestId").isNumber())
            .andExpect(jsonPath("$.body").value("Delivery window confirmed."))
            .andExpect(jsonPath("$.visibleToBuyer").value(true))
            .andReturn().getResponse().getContentAsString();

        long messageId = objectMapper.readTree(body).path("id").asLong();
        mockMvc.perform(get("/api/v1/conversation-messages/{id}", messageId).header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.senderName").value("Sales Lead"));
    }

    private void validateCommercially(String requestId) throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests/{id}/commercial-validations", requestId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "commercialOwner": "Sales Lead", "comments": "Credit and stock validated." }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("commercially_validated"));
    }
}
