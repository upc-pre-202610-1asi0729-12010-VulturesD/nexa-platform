package com.nexa.platform.shared.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSurfaceSecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {
        "/api/v1/categories",
        "/api/v1/promotions",
        "/api/v1/inventory",
        "/api/v1/inventory/alerts",
        "/api/v1/warehouses",
        "/api/v1/shipments",
        "/api/v1/users",
        "/api/v1/audit-logs",
        "/api/v1/client-accounts",
        "/api/v1/dispatch-orders"
    })
    void operationalReadsRequireAuthentication(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint)).andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/api/v1/health",
        "/v3/api-docs",
        "/api/v1/tenants/preview/icisa"
    })
    void explicitPublicReadsRemainAvailable(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/api/v1/auth/login",
        "/api/v1/authentication/sign-in",
        "/api/v1/organization-registrations"
    })
    void explicitPublicPostsReachTheirControllers(String endpoint) throws Exception {
        mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(result -> {
                int status = result.getResponse().getStatus();
                if (status == 401 || status == 403) {
                    throw new AssertionError(endpoint + " was blocked by authentication with status " + status);
                }
            });
    }
}
