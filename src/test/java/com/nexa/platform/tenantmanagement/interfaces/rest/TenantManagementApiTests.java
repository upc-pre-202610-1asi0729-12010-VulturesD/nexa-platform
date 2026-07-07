package com.nexa.platform.tenantmanagement.interfaces.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TenantManagementApiTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void organizationRegistrationPostIsPublicLikeAppsWeb() throws Exception {
        mockMvc.perform(post("/api/v1/organization-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "REG-TEST-001",
                      "company": { "legalName": "Inversiones Frias SAC" },
                      "workspace": { "workspaceName": "ICISA", "workspaceSlug": "icisa-test" },
                      "administrator": { "email": "admin@icisa.test" },
                      "operation": { "operationType": "b2bColdChainDistributor", "minTemperature": 2, "maxTemperature": 8 },
                      "location": { "facilityName": "Almacen Callao", "fefoEnabled": true },
                      "registrationProfile": { "plan": "Professional", "termsAccepted": true }
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.externalId").value("REG-TEST-001"))
            .andExpect(jsonPath("$.status").value("pending_review"))
            .andExpect(jsonPath("$.companyName").value("Inversiones Frias SAC"));
    }

    @Test
    void tenantAdministrationReadsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/tenant-members?tenantId=1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void tenantAndWorkspaceCanBeCreatedAndResolvedBySlug() throws Exception {
        String tenantBody = mockMvc.perform(post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Test Cold Chain",
                      "legalName": "Test Cold Chain SAC",
                      "slug": "test-cold-chain",
                      "ruc": "20999999991",
                      "workspaceUrl": "https://test-cold-chain.nexa.pe",
                      "emailDomain": "test-cold-chain.pe",
                      "plan": "Business",
                      "status": "active",
                      "country": "PE"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.slug").value("test-cold-chain"))
            .andReturn().getResponse().getContentAsString();
        long tenantId = objectMapper.readTree(tenantBody).path("id").asLong();

        mockMvc.perform(post("/api/v1/workspaces")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tenantId": %d,
                      "name": "Test Cold Chain Main Workspace",
                      "slug": "test-cold-chain-main",
                      "url": "https://test-cold-chain.nexa.pe",
                      "emailDomain": "test-cold-chain.pe",
                      "status": "active",
                      "primaryWorkspace": true
                    }
                    """.formatted(tenantId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value("test-cold-chain-main"));

        mockMvc.perform(get("/api/v1/tenants/preview/test-cold-chain-main"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value("test-cold-chain"))
            .andExpect(jsonPath("$.workspaceUrl").value("https://test-cold-chain.nexa.pe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void companyAdministrationResourcesPreserveAppsWebContract() throws Exception {
        mockMvc.perform(post("/api/v1/tenant-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tenantId":1,"code":"dispatch-evidence","name":"Dispatch evidence required",
                     "description":"POD is mandatory","category":"Dispatch","enabled":true}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value("Dispatch"))
            .andExpect(jsonPath("$.enabled").value(true));

        mockMvc.perform(post("/api/v1/tenant-custom-fields")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tenantId":1,"code":"storage-zone","label":"Storage zone",
                     "targetResource":"Warehouse","fieldType":"Text","required":true,"enabled":true}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("storage-zone"))
            .andExpect(jsonPath("$.targetResource").value("Warehouse"))
            .andExpect(jsonPath("$.enabled").value(true));

        mockMvc.perform(post("/api/v1/tenant-subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tenantId":1,"plan":"Professional","seats":50,"warehouses":3,
                     "paymentStatus":"review_active","nextBillingDate":"2026-08-01",
                     "billingContact":"billing@icisa.pe"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plan").value("Professional"))
            .andExpect(jsonPath("$.seats").value(50))
            .andExpect(jsonPath("$.warehouses").value(3))
            .andExpect(jsonPath("$.billingContact").value("billing@icisa.pe"));

        String userBody = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"new.logistics","email":"new.logistics@icisa.pe",
                     "password":"TempMember123!","role":"Logistics Manager","fullName":"New Logistics"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("new.logistics@icisa.pe"))
            .andReturn().getResponse().getContentAsString();
        long userId = objectMapper.readTree(userBody).path("id").asLong();

        mockMvc.perform(post("/api/v1/user-workspace-memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tenantId":1,"workspaceId":1,"userId":%d,"email":"new.logistics@icisa.pe",
                     "fullName":"New Logistics","role":"Logistics Manager","department":"Logistics",
                     "status":"active","portalAccess":"internal","clientAccountId":null}
                    """.formatted(userId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.workspaceId").value(1));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"new.logistics@icisa.pe\",\"password\":\"TempMember123!\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.workspaceId").value(1));
    }

    @Test
    void companyAdministrationRejectsWrongTenantAndNonOwnerWrites() throws Exception {
        String adminAuth = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@nexa.local\",\"password\":\"NexaAdmin123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        String adminToken = objectMapper.readTree(adminAuth).path("token").asText();

        mockMvc.perform(get("/api/v1/tenant-rules")
                .queryParam("tenantId", "999")
                .header("Authorization", "Bearer " + adminToken)
                .header("X-Nexa-Tenant-Id", "999"))
            .andExpect(status().isForbidden());

        String salesAuth = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"sales@nexa.com\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        String salesToken = objectMapper.readTree(salesAuth).path("token").asText();

        mockMvc.perform(post("/api/v1/tenant-rules")
                .header("Authorization", "Bearer " + salesToken)
                .header("X-Nexa-Tenant-Id", "1")
                .header("X-Nexa-Workspace", "icisa")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"tenantId":1,"code":"sales-write","name":"Forbidden sales write",
                     "description":"Must not persist","category":"Tenant","enabled":true}
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    void apiDocsListTenantManagementContractEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/api/v1/organization-registrations")))
            .andExpect(content().string(containsString("/api/v1/workspaces")));
    }
}
