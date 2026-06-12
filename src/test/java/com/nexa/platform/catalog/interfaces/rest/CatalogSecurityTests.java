package com.nexa.platform.catalog.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogSecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void productsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/products")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void productsExposeCatalogAssetFields() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productCode").exists())
            .andExpect(jsonPath("$[0].imageUrl").exists())
            .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }
}
