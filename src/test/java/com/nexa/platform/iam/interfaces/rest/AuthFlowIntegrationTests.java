package com.nexa.platform.iam.interfaces.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerReturnsBearerToken() throws Exception {
        String body = "{\"fullName\":\"Warehouse Operator\",\"email\":\"operator.flow@nexa.local\",\"password\":\"Operator123\"}";
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.email").value("operator.flow@nexa.local"));
    }

    @Test
    void loginReturnsCommercialProfileContract() throws Exception {
        String body = "{\"email\":\"sales@nexa.com\",\"password\":\"NexaDemo2026!\"}";
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.fullName").value("Valeria Sanchez"))
            .andExpect(jsonPath("$.user.roles", hasItem("ROLE_SALES")))
            .andExpect(jsonPath("$.user.profile").value("commercial"))
            .andExpect(jsonPath("$.user.scope").value("commercial"))
            .andExpect(jsonPath("$.user.segment").value("S1 Commercial"));
    }

    @Test
    void loginReturnsBuyerPortalContractAndCurrentUserProfile() throws Exception {
        String body = "{\"email\":\"buyer.demo@nexa.com\",\"password\":\"NexaDemo2026!\"}";
        String token = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.fullName").value("Elena Litano"))
            .andExpect(jsonPath("$.user.roles", hasItem("ROLE_BUYER")))
            .andExpect(jsonPath("$.user.profile").value("buyer"))
            .andExpect(jsonPath("$.user.scope").value("portal"))
            .andExpect(jsonPath("$.user.clientId").value("CLI-001"))
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll("^.*\\\"token\\\":\\\"([^\\\"]+)\\\".*$", "$1");

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("buyer.demo@nexa.com"))
            .andExpect(jsonPath("$.scope").value("portal"));
    }
}
