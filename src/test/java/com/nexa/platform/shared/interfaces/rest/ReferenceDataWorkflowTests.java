package com.nexa.platform.shared.interfaces.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ReferenceDataWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void referenceDataMatchesAppsWebRoutes() throws Exception {
        String token = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/reference/countries").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("PE"));

        mockMvc.perform(get("/api/v1/reference/departments").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].parentCode").value("PE"));

        mockMvc.perform(get("/api/v1/reference/provinces").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].code", hasItem("callao")));

        mockMvc.perform(get("/api/v1/reference/districts").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(35)))
            .andExpect(jsonPath("$[*].code", hasItem("miraflores")));

        mockMvc.perform(get("/api/v1/reference/payment-options").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].code", hasItem("bank_transfer")))
            .andExpect(jsonPath("$[*].code", hasItem("cash_on_delivery")));

        mockMvc.perform(get("/api/v1/reference/delivery-methods").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].code", hasItem("scheduled_route")));

        mockMvc.perform(get("/api/v1/reference/units-of-measure").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].code", hasItem("kg")));

        mockMvc.perform(get("/api/v1/reference/statuses").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.purchaseRequests", hasItem("commercially_validated")))
            .andExpect(jsonPath("$.dispatchOrders", hasItem("in_route")))
            .andExpect(jsonPath("$.payments", hasItem("confirmed")));

        mockMvc.perform(get("/api/v1/reference/document-types").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", notNullValue()));
    }

    @Test
    void referenceDataRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/reference/countries"))
            .andExpect(status().isForbidden());
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
