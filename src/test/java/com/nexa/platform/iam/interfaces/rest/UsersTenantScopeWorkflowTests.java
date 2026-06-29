package com.nexa.platform.iam.interfaces.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.Tenant;
import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import com.nexa.platform.tenantmanagement.domain.model.Workspace;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UsersTenantScopeWorkflowTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserAccountRepositoryPort users;
    @Autowired private TenantRepositoryPort tenants;
    @Autowired private WorkspaceRepositoryPort workspaces;
    @Autowired private UserWorkspaceMembershipRepositoryPort memberships;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void adminListAndDetailAreDerivedFromAuthenticatedTenant() throws Exception {
        UserAccount external = createExternalTenantUser();
        UserAccount sales = users.findByEmail("sales@nexa.com").orElseThrow();
        String token = login("admin@nexa.local", "NexaAdmin123", "icisa");

        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].email", hasItem("sales@nexa.com")))
            .andExpect(jsonPath("$[*].email", hasItem("admin@nexa.local")))
            .andExpect(jsonPath("$[*].email", not(hasItem("external@other.test"))));

        mockMvc.perform(get("/api/v1/users/{id}", sales.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.workspaceSlug").value("icisa"));

        mockMvc.perform(get("/api/v1/users/{id}", external.getId())
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    void nonAdminCannotListOrReadManagedUsers() throws Exception {
        UserAccount buyer = users.findByEmail("buyer@nexa.com").orElseThrow();
        String token = login("sales@nexa.com", "NexaAccess2026!", "icisa");

        mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/users/{id}", buyer.getId()).header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    private UserAccount createExternalTenantUser() {
        Tenant tenant = tenants.save(new Tenant("Other", "Other Tenant S.A.", "other-tenant", "20999999999",
            "https://other.nexa.test", "other.test", "Business", "active", "PE"));
        Workspace workspace = workspaces.save(new Workspace(tenant.getId(), "Other Workspace", "other-workspace",
            "https://other.nexa.test", "other.test", "active", true));
        UserAccount account = users.save(new UserAccount("External User", "external@other.test",
            passwordEncoder.encode("ExternalAccess2026!")));
        memberships.save(new UserWorkspaceMembership(tenant.getId(), workspace.getId(), account.getId(),
            account.getEmail(), account.getFullName(), "sales", "Sales", "active", "internal", null));
        return account;
    }

    private String login(String email, String password, String workspaceSlug) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of(
                    "email", email, "password", password, "workspaceSlug", workspaceSlug))))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("token").asText();
    }
}
