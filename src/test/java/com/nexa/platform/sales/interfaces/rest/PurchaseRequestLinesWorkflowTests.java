package com.nexa.platform.sales.interfaces.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
class PurchaseRequestLinesWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buyerCanManageOwnPurchaseRequestLinesThroughAppsWebRoute() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/purchase-request-lines").header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThan(0)))
            .andExpect(jsonPath("$[0].purchaseRequestId").value(1));

        String created = mockMvc.perform(post("/api/v1/purchase-request-lines")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(lineBody(1L, 1L, new BigDecimal("2"), "UN", new BigDecimal("1.25"), "Buyer add-on")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.purchaseRequestId").value(1))
            .andExpect(jsonPath("$.catalogItemId").value(1))
            .andExpect(jsonPath("$.quantity").value(2))
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).path("id").asLong();

        mockMvc.perform(put("/api/v1/purchase-request-lines/{id}", id)
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(lineBody(1L, 2L, new BigDecimal("4"), "box", new BigDecimal("2.50"), "Adjusted")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(4))
            .andExpect(jsonPath("$.unit").value("box"))
            .andExpect(jsonPath("$.notes").value("Adjusted"));

        mockMvc.perform(delete("/api/v1/purchase-request-lines/{id}", id)
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/purchase-request-lines/{id}", id)
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isNotFound());
    }

    @Test
    void buyerCanCreatePurchaseRequestAndImmediatelyAttachLines() throws Exception {
        String buyerToken = login("elena.litano@icisa.pe", "Password123!");

        String createdRequest = mockMvc.perform(post("/api/v1/purchase-requests")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "priority": "high",
                      "requestedDeliveryDate": "2026-07-20",
                      "deliveryAddress": "Av. Argentina 2450, Callao",
                      "deliveryDistrict": "Callao",
                      "deliveryCity": "Callao",
                      "paymentOption": "credit_line",
                      "comments": "Buyer portal request"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.backendId").isNumber())
            .andExpect(jsonPath("$.requestId").isNumber())
            .andExpect(jsonPath("$.clientId").value("CLI-001"))
            .andReturn().getResponse().getContentAsString();

        long purchaseRequestId = objectMapper.readTree(createdRequest).path("backendId").asLong();
        mockMvc.perform(post("/api/v1/purchase-request-lines")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(lineBody(purchaseRequestId, 1L, new BigDecimal("3"), "UN", new BigDecimal("1.50"), "Buyer line")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.purchaseRequestId").value(purchaseRequestId))
            .andExpect(jsonPath("$.catalogItemId").value(1))
            .andExpect(jsonPath("$.quantity").value(3));

        mockMvc.perform(get("/api/v1/purchase-requests/{id}", purchaseRequestId)
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].qty").value(3));
    }

    @Test
    void lineCrudRejectsInvalidQuantityAndTenantHeaderMismatch() throws Exception {
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(post("/api/v1/purchase-request-lines")
                .header("Authorization", bearer(salesToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(lineBody(1L, 1L, BigDecimal.ZERO, "UN", BigDecimal.ZERO, "")))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/purchase-request-lines")
                .header("Authorization", bearer(salesToken))
                .header("X-Nexa-Tenant-Id", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lineBody(1L, 1L, BigDecimal.ONE, "UN", BigDecimal.ZERO, "")))
            .andExpect(status().isForbidden());
    }

    private String lineBody(Long purchaseRequestId, Long catalogItemId, BigDecimal quantity, String unit,
                            BigDecimal estimatedWeightKg, String notes) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
            "purchaseRequestId", purchaseRequestId,
            "catalogItemId", catalogItemId,
            "quantity", quantity,
            "unit", unit,
            "estimatedWeightKg", estimatedWeightKg,
            "notes", notes));
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
