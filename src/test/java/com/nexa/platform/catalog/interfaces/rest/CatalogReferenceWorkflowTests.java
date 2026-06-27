package com.nexa.platform.catalog.interfaces.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.StreamSupport;
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
class CatalogReferenceWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void brandsSupportAuthenticatedReadsAndAdminManagedSoftDelete() throws Exception {
        String salesToken = login("sales@nexa.com", "NexaAccess2026!");
        String adminToken = login("admin@nexa.local", "NexaAdmin123");

        mockMvc.perform(get("/api/v1/brands").header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThan(0)));

        mockMvc.perform(post("/api/v1/brands")
                .header("Authorization", bearer(salesToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("Restricted Brand", "Must not be created")))
            .andExpect(status().isForbidden());

        String created = mockMvc.perform(post("/api/v1/brands")
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("A1 Cold Brand", "Imported cold-chain portfolio")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.isActive").value(true))
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).path("id").asLong();

        mockMvc.perform(get("/api/v1/brands").queryParam("name", "a1 cold brand")
                .header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(put("/api/v1/brands/{id}", id)
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("A1 Cold Brand Updated", "Updated description")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("A1 Cold Brand Updated"));

        mockMvc.perform(delete("/api/v1/brands/{id}", id)
                .header("Authorization", bearer(adminToken)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/brands/{id}", id).header("Authorization", bearer(salesToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void categoriesSupportAdminCrudAndRejectDuplicateNames() throws Exception {
        String adminToken = login("admin@nexa.local", "NexaAdmin123");

        String created = mockMvc.perform(post("/api/v1/categories")
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("A1 Specialty", "Specialty cold-chain category")))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).path("id").asLong();

        mockMvc.perform(post("/api/v1/categories")
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("a1 specialty", "Duplicate")))
            .andExpect(status().isConflict());

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(referenceBody("A1 Specialty Updated", "Updated")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("A1 Specialty Updated"));

        mockMvc.perform(delete("/api/v1/categories/{id}", id)
                .header("Authorization", bearer(adminToken)))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/categories/{id}", id).header("Authorization", bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void promotionalCatalogMatchesPagedAppsWebReadModel() throws Exception {
        String buyerToken = login("buyer@nexa.com", "NexaAccess2026!");

        mockMvc.perform(get("/api/v1/catalog/promotional-catalog")
                .queryParam("page", "1")
                .queryParam("pageSize", "1")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.pageSize").value(1))
            .andExpect(jsonPath("$.totalItems", greaterThan(1)))
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].currency").value("PEN"));

        String fullCatalog = mockMvc.perform(get("/api/v1/catalog/promotional-catalog")
                .queryParam("pageSize", "100")
                .header("Authorization", bearer(buyerToken)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        JsonNode promoted = StreamSupport.stream(objectMapper.readTree(fullCatalog).path("items").spliterator(), false)
            .filter(item -> "PROD-0013".equals(item.path("productId").asText()))
            .findFirst()
            .orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("PROMO-COLD-001",
            promoted.path("activePromotionCode").asText());
    }

    private String referenceBody(String name, String description) throws Exception {
        return objectMapper.writeValueAsString(Map.of("name", name, "description", description));
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
