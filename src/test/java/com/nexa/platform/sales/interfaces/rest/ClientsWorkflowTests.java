package com.nexa.platform.sales.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void clientAccountsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/client-accounts").header(TENANT_HEADER, 1))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void clientAccountCrudAndFinancialProfileMatchAppsWebContract() throws Exception {
        String body = mockMvc.perform(post("/api/v1/client-accounts")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "code": "CLI-TEST-001",
                      "businessName": "Frio Norte SAC",
                      "commercialName": "Frio Norte",
                      "taxId": "20999999991",
                      "segment": "distributor",
                      "contact": "Ana Torres",
                      "contactEmail": "ana@frionorte.test",
                      "phone": "999111222",
                      "deliveryAddress": "Av. Refrigeracion 100",
                      "paymentCondition": "credit_15",
                      "monthlyCreditLimit": 10000.00,
                      "monthlyCreditUsed": 2500.00,
                      "monthlyCreditStatus": "ok",
                      "portalAccess": true,
                      "status": "active"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/client-accounts/\\d+")))
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.code").value("CLI-TEST-001"))
            .andExpect(jsonPath("$.ruc").value("20999999991"))
            .andExpect(jsonPath("$.monthlyCreditAvailable").value(7500.00))
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(body).path("id").asLong();

        mockMvc.perform(patch("/api/v1/client-accounts/{id}", id)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "businessName": "Frio Norte SAC",
                      "commercialName": "Frio Norte B2B",
                      "taxId": "20999999991",
                      "segment": "distributor",
                      "contact": "Ana Torres",
                      "contactEmail": "ana@frionorte.test",
                      "phone": "999111222",
                      "deliveryAddress": "Av. Refrigeracion 100",
                      "paymentCondition": "credit_30",
                      "monthlyCreditLimit": 12000.00,
                      "monthlyCreditUsed": 3000.00,
                      "monthlyCreditStatus": "ok",
                      "portalAccess": true,
                      "status": "active"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.commercialName").value("Frio Norte B2B"))
            .andExpect(jsonPath("$.paymentCondition").value("credit_30"))
            .andExpect(jsonPath("$.monthlyCreditAvailable").value(9000.00));

        mockMvc.perform(get("/api/v1/client-accounts/{id}/financial-profile", id)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.monthlyCreditLimit").value(12000.00))
            .andExpect(jsonPath("$.monthlyCreditAvailable").value(9000.00));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void clientAccountsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/client-accounts/1").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/client-accounts").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
