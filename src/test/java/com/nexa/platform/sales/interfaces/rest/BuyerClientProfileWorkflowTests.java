package com.nexa.platform.sales.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class BuyerClientProfileWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void buyerTokenOnlyReadsAndUpdatesItsOwnClientAccount() throws Exception {
        String token = buyerToken();

        mockMvc.perform(get("/api/v1/client-accounts").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1));

        mockMvc.perform(get("/api/v1/client-accounts/2").header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/v1/client-accounts/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"businessName\":\"Blocked direct update\",\"taxId\":\"20600000001\"}"))
            .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/profile/client-account")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "businessName": "Importaciones y Comercio Internacional S.A.",
                      "commercialName": "ICISA",
                      "taxId": "20600000001",
                      "segment": "importer",
                      "contact": "Elena Litano",
                      "contactEmail": "compras@icisa.pe",
                      "phone": "999000111",
                      "deliveryAddress": "Av. Argentina 2450, Callao",
                      "paymentCondition": "credit_15",
                      "monthlyCreditLimit": 50000.00,
                      "monthlyCreditUsed": 12000.00,
                      "monthlyCreditStatus": "ok",
                      "portalAccess": true,
                      "status": "active"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.commercialName").value("ICISA"))
            .andExpect(jsonPath("$.monthlyCreditAvailable").value(38000.00));
    }

    private String buyerToken() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"buyer@nexa.com\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("token").asText();
    }
}
