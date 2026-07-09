package com.nexa.platform.promotions.interfaces.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
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
class PromotionsWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void salesCanManagePromotionLifecycleThroughAppsWebRoute() throws Exception {
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/promotions").header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThan(0)))
            .andExpect(jsonPath("$[0].tenantId").value(1));

        String created = mockMvc.perform(post("/api/v1/promotions")
                .header("Authorization", bearer(salesToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("PROMO-A1-TEST", "A1 seasonal rotation", "draft")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.code").value("PROMO-A1-TEST"))
            .andExpect(jsonPath("$.status").value("draft"))
            .andExpect(jsonPath("$.productIds", hasItem("PROD-0001")))
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).path("id").asLong();

        mockMvc.perform(put("/api/v1/promotions/{id}/activate", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("active"));

        mockMvc.perform(put("/api/v1/promotions/{id}/pause", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("paused"));

        mockMvc.perform(put("/api/v1/promotions/{id}/archive", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("archived"));

        mockMvc.perform(delete("/api/v1/promotions/{id}", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/promotions/{id}", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isNotFound());
    }

    @Test
    void buyerCannotManagePromotions() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");

        mockMvc.perform(post("/api/v1/promotions")
                .header("Authorization", bearer(buyerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("PROMO-A1-BUYER", "Buyer blocked promotion", "draft")))
            .andExpect(status().isForbidden());
    }

    private String body(String code, String name, String status) throws Exception {
        return objectMapper.writeValueAsString(Map.ofEntries(
            entry("code", code),
            entry("name", name),
            entry("campaign", "Buyer portal parity"),
            entry("description", "Promotion lifecycle parity"),
            entry("discountLabel", "8% adjustment"),
            entry("visibility", "buyer_portal"),
            entry("commercialRule", "Seasonal stock rotation"),
            entry("adjustmentType", "percentage"),
            entry("targetSegment", "food_service"),
            entry("notes", "A1 test"),
            entry("catalogScope", "selected_products"),
            entry("startDate", "2026-07-01"),
            entry("endDate", "2026-07-31"),
            entry("status", status),
            entry("productIds", List.of("PROD-0001"))));
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
