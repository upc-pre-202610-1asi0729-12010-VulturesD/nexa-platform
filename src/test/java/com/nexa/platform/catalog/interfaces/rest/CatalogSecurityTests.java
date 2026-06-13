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
            .andExpect(jsonPath("$.length()").value(50))
            .andExpect(jsonPath("$[0].productCode").exists())
            .andExpect(jsonPath("$[0].sku").value("PROD-0001"))
            .andExpect(jsonPath("$[0].name").value("QUESO GRANA PADANO DOP 150G"))
            .andExpect(jsonPath("$[0].imageUrl").exists())
            .andExpect(jsonPath("$[0].brand").value("Agriform"))
            .andExpect(jsonPath("$[0].availability").value("AVAILABLE"))
            .andExpect(jsonPath("$[0].temperatureRange").value("2C - 8C"))
            .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void catalogItemsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/catalog-items")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void catalogItemsExposeSourceCompatibleContract() throws Exception {
        mockMvc.perform(get("/api/v1/catalog-items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(50))
            .andExpect(jsonPath("$[0].sku").value("PROD-0001"))
            .andExpect(jsonPath("$[0].productCode").value("PROD-0001"))
            .andExpect(jsonPath("$[0].brandName").value("Agriform"))
            .andExpect(jsonPath("$[0].brand").value("Agriform"))
            .andExpect(jsonPath("$[0].price").value(17.3))
            .andExpect(jsonPath("$[0].itemName").value("QUESO GRANA PADANO DOP 150G"))
            .andExpect(jsonPath("$[0].unitPriceAmount").value(17.3))
            .andExpect(jsonPath("$[0].availableStock").value(160))
            .andExpect(jsonPath("$[0].imageUrl").value("/assets/catalog-items/Agriform_QUESO_GRANA_PADANO_DOP_150G.jpeg"));
    }
}
