package com.nexa.platform.warehouse.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class InventoryOperationsWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void inventoryLotsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/inventory-lots").header(TENANT_HEADER, "1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE")
    void warehouseAndInventoryReadsAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/warehouses").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").isNumber());

        mockMvc.perform(get("/api/v1/warehouses").header(TENANT_HEADER, "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/inventory").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").isNumber());

        mockMvc.perform(get("/api/v1/inventory").header(TENANT_HEADER, "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE")
    void lotReservationAndReleaseMatchAppsWebContract() throws Exception {
        mockMvc.perform(get("/api/v1/inventory-lots").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("LOT-ICISA-001"))
            .andExpect(jsonPath("$[0].qty").value(120))
            .andExpect(jsonPath("$[0].reserved").value(10));

        String created = mockMvc.perform(post("/api/v1/reservations")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "code": "RES-2026-TEST",
                      "inventoryItemId": 1,
                      "lotCode": "LOT-ICISA-001",
                      "orderId": "ORD-2026-0001",
                      "units": 5
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/reservations/\\d+")))
            .andExpect(jsonPath("$.status").value("reserved"))
            .andExpect(jsonPath("$.units").value(5))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long reservationId = objectMapper.readTree(created).get("id").asLong();
        mockMvc.perform(get("/api/v1/inventory-lots/LOT-ICISA-001").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reserved").value(15));

        mockMvc.perform(post("/api/v1/reservations")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "code": "RES-OVER",
                      "inventoryItemId": 1,
                      "lotCode": "LOT-ICISA-001",
                      "units": 200
                    }
                    """))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/reservations/{id}/releases", reservationId)
                .header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("released"));

        mockMvc.perform(get("/api/v1/inventory-lots/LOT-ICISA-001").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reserved").value(10));

        mockMvc.perform(get("/api/v1/inventory-lots/LOT-ICISA-001").header(TENANT_HEADER, "2"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE")
    void inventoryMovementLedgerMatchesAppsWebContractAndTenantScope() throws Exception {
        mockMvc.perform(post("/api/v1/stock-movements")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "code": "STM-TEST-ENTRY",
                      "inventoryItemId": 1,
                      "lotId": "LOT-ICISA-001",
                      "type": "entry",
                      "quantity": 5,
                      "reason": "Supplier receipt",
                      "note": "Cold-chain reception verified",
                      "temperatureReading": 3.80,
                      "user": "Warehouse operator"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/inventory-movements/STM-TEST-ENTRY"))
            .andExpect(jsonPath("$.tenantId").value(1))
            .andExpect(jsonPath("$.code").value("STM-TEST-ENTRY"))
            .andExpect(jsonPath("$.type").value("entry"))
            .andExpect(jsonPath("$.qty").value(5));

        mockMvc.perform(get("/api/v1/inventory-movements/STM-TEST-ENTRY").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lotId").value("LOT-ICISA-001"))
            .andExpect(jsonPath("$.productId").isNotEmpty());

        mockMvc.perform(get("/api/v1/stock-movements").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("STM-TEST-ENTRY"));

        mockMvc.perform(get("/api/v1/inventory-lots/LOT-ICISA-001").header(TENANT_HEADER, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.qty").value(125));

        mockMvc.perform(get("/api/v1/stock-movements").header(TENANT_HEADER, "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/api/v1/inventory-movements/STM-TEST-ENTRY").header(TENANT_HEADER, "2"))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/stock-movements")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"STM-TEST-ENTRY\",\"inventoryItemId\":1,\"type\":\"entry\",\"quantity\":1}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "BUYER")
    void buyerCannotCreateInventoryMovement() throws Exception {
        mockMvc.perform(post("/api/v1/stock-movements")
                .header(TENANT_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"inventoryItemId\":1,\"type\":\"entry\",\"quantity\":1}"))
            .andExpect(status().isForbidden());
    }
}
