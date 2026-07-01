package com.nexa.platform.sales.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PurchaseRequestsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void commercialValidationRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests/PR-2026-0001/commercial-validations")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "commercialOwner": "Sales Lead", "comments": "Credit and stock validated." }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void buyerCannotValidatePurchaseRequestCommercially() throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests/PR-2026-0001/commercial-validations")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "commercialOwner": "Buyer", "comments": "Should not be allowed." }
                    """))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void purchaseRequestCanBeCreatedWithItems() throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "clientId": "CLI-001",
                      "priority": "high",
                      "requestedDeliveryDate": "2026-06-20",
                      "deliveryAddress": "Av. Argentina 2450, Callao",
                      "paymentOption": "credit_line",
                      "comments": "Buyer portal request",
                      "items": [
                        { "productId": "PROD-0001", "quantity": 2, "unit": "UN" }
                      ]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.code").isString())
            .andExpect(jsonPath("$.clientId").value("CLI-001"))
            .andExpect(jsonPath("$.status").value("submitted"))
            .andExpect(jsonPath("$.items[0].productId").value("PROD-0001"))
            .andExpect(jsonPath("$.items[0].qty").value(2));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void commercialValidationUpdatesPurchaseRequestState() throws Exception {
        mockMvc.perform(post("/api/v1/purchase-requests/PR-2026-0001/commercial-validations")
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "commercialOwner": "Sales Lead", "comments": "Credit and stock validated." }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("PR-2026-0001"))
            .andExpect(jsonPath("$.status").value("commercially_validated"))
            .andExpect(jsonPath("$.commercialOwner").value("Sales Lead"))
            .andExpect(jsonPath("$.comments").value("Credit and stock validated."));

        mockMvc.perform(get("/api/v1/purchase-requests/PR-2026-0001").header(TENANT_HEADER, 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.code").value("PR-2026-0001"))
            .andExpect(jsonPath("$.status").value("commercially_validated"))
            .andExpect(jsonPath("$.commercialOwner").value("Sales Lead"));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void purchaseRequestsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/purchase-requests/PR-2026-0001").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/purchase-requests").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }
}
