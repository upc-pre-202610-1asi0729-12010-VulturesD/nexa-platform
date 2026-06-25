package com.nexa.platform.iam.interfaces.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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
        String body = "{\"email\":\"sales@nexa.com\",\"password\":\"NexaAccess2026!\",\"workspaceSlug\":\"icisa\"}";
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
    void compatibilitySignInReturnsRealJwtForSwaggerUsers() throws Exception {
        String body = "{\"email\":\"elena.litano@icisa.pe\",\"username\":\"elena.litano@icisa.pe\",\"password\":\"Password123!\",\"workspaceSlug\":\"icisa\"}";
        mockMvc.perform(post("/api/v1/authentication/sign-in").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.email").value("elena.litano@icisa.pe"))
            .andExpect(jsonPath("$.user.workspaceSlug").value("icisa"))
            .andExpect(jsonPath("$.user.roles", hasItem("ROLE_BUYER")));
    }

    @Test
    void workspaceLoginRejectsUnknownWorkspaceWithoutLeakingMembershipDetails() throws Exception {
        String body = "{\"email\":\"sales@nexa.com\",\"password\":\"NexaAccess2026!\",\"workspaceSlug\":\"unknown-workspace\"}";
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.details[0]").value("Invalid credentials"));
    }

    @Test
    void loginReturnsBuyerPortalContractAndCurrentUserProfile() throws Exception {
        String body = "{\"email\":\"buyer@nexa.com\",\"password\":\"NexaAccess2026!\"}";
        String token = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.fullName").value("Elena Litano"))
            .andExpect(jsonPath("$.user.roles", hasItem("ROLE_BUYER")))
            .andExpect(jsonPath("$.user.profile").value("buyer"))
            .andExpect(jsonPath("$.user.scope").value("portal"))
            .andExpect(jsonPath("$.user.clientId").value("CLI-001"))
            .andExpect(jsonPath("$.user.tenantId").value(1))
            .andExpect(jsonPath("$.user.workspaceId").value(1))
            .andExpect(jsonPath("$.user.workspaceSlug").value("icisa"))
            .andExpect(jsonPath("$.user.membershipStatus").value("active"))
            .andExpect(jsonPath("$.user.clientAccountId").value(1))
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll("^.*\\\"token\\\":\\\"([^\\\"]+)\\\".*$", "$1");

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("buyer@nexa.com"))
            .andExpect(jsonPath("$.scope").value("portal"));

        mockMvc.perform(get("/api/v1/payment-method-records")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tenantId").value(1));

        mockMvc.perform(get("/api/v1/payment-method-records")
                .header("Authorization", "Bearer " + token)
                .header("X-Nexa-Tenant-Id", "2"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/payment-method-records")
                .header("Authorization", "Bearer " + token)
                .header("X-Nexa-Workspace", "other-workspace"))
            .andExpect(status().isForbidden());
    }

    @Test
    void userWithoutWorkspaceMembershipCannotTrustTenantHeader() throws Exception {
        String registration = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "fullName": "Unscoped Operator",
                      "email": "unscoped.operator@nexa.local",
                      "password": "Operator123"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(registration).path("token").asText();

        mockMvc.perform(get("/api/v1/payment-method-records")
                .header("Authorization", "Bearer " + token)
                .header("X-Nexa-Tenant-Id", "1"))
            .andExpect(status().isForbidden());
    }
}
