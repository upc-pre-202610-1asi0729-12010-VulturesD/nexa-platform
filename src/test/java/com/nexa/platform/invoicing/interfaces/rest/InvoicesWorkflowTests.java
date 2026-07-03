package com.nexa.platform.invoicing.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
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
class InvoicesWorkflowTests {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void invoicesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/invoices").header(TENANT_HEADER, 1))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SALES")
    void unpaidInvoiceCanBeUpdatedAndPaidButNotVoidedAfterward() throws Exception {
        long invoiceId = createInvoice(1, "INV-TEST-PAID");

        mockMvc.perform(patch("/api/v1/invoices/{id}", invoiceId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceNumber": "INV-TEST-PAID-UPDATED",
                      "orderId": 2,
                      "currency": "USD",
                      "lines": [
                        { "description": "Updated cold-chain product", "quantity": 2, "unitPrice": 75.50 }
                      ]
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.invoiceNumber").value("INV-TEST-PAID-UPDATED"))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.total").value(151.00))
            .andExpect(jsonPath("$.status").value("issued"));

        mockMvc.perform(post("/api/v1/invoices/{id}/status-changes", invoiceId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"paymentStatus\": \"paid\" }"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("paid"))
            .andExpect(jsonPath("$.paidAt").isNotEmpty());

        mockMvc.perform(post("/api/v1/invoices/{id}/voidings", invoiceId)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Paid invoices cannot be voided."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void voidedInvoiceCannotBePaidOrUpdated() throws Exception {
        long invoiceId = createInvoice(1, "INV-TEST-VOID");

        mockMvc.perform(post("/api/v1/invoices/{id}/voidings", invoiceId)
                .header(TENANT_HEADER, 1))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/invoices/{id}/status-changes", invoiceId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"paymentStatus\": \"paid\" }"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Voided invoices cannot be paid."));

        mockMvc.perform(patch("/api/v1/invoices/{id}", invoiceId)
                .header(TENANT_HEADER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceNumber": "INV-TEST-VOID-UPDATED",
                      "orderId": 2,
                      "currency": "PEN",
                      "lines": [
                        { "description": "Invalid update", "quantity": 1, "unitPrice": 10.00 }
                      ]
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Voided invoices cannot be updated."));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void invoicesAreTenantScoped() throws Exception {
        mockMvc.perform(get("/api/v1/invoices/2").header(TENANT_HEADER, 2))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/invoices").header(TENANT_HEADER, 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buyerJwtReadsOnlyOwnInvoices() throws Exception {
        String otherInvoice = "INV-OTHER-CLIENT";
        mockMvc.perform(post("/api/v1/invoices")
                .header(HttpHeaders.AUTHORIZATION, authorization("sales@nexa.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceNumber": "%s",
                      "orderId": 2,
                      "currency": "PEN",
                      "lines": [
                        { "description": "Other client product", "quantity": 1, "unitPrice": 10.00 }
                      ]
                    }
                    """.formatted(otherInvoice)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/invoices")
                .header(HttpHeaders.AUTHORIZATION, authorization("buyer@nexa.com")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].invoiceNumber", not(hasItem(otherInvoice))));
    }

    @Test
    @WithMockUser(roles = "SALES")
    void invoiceCannotReferenceAnOrderFromAnotherTenant() throws Exception {
        mockMvc.perform(post("/api/v1/invoices")
                .header(TENANT_HEADER, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceNumber": "INV-CROSS-TENANT",
                      "orderId": 1,
                      "currency": "PEN",
                      "lines": [
                        { "description": "Invalid relation", "quantity": 1, "unitPrice": 10.00 }
                      ]
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    private long createInvoice(long tenantId, String invoiceNumber) throws Exception {
        String body = mockMvc.perform(post("/api/v1/invoices")
                .header(TENANT_HEADER, tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "invoiceNumber": "%s",
                      "orderId": 2,
                      "currency": "PEN",
                      "lines": [
                        { "description": "Cold-chain product", "quantity": 1, "unitPrice": 100.00 }
                      ]
                    }
                    """.formatted(invoiceNumber)))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, matchesPattern(".*/api/v1/invoices/\\d+")))
            .andExpect(jsonPath("$.tenantId").value(tenantId))
            .andExpect(jsonPath("$.status").value("issued"))
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).path("id").asLong();
    }

    private String authorization(String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"NexaAccess2026!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return "Bearer " + objectMapper.readTree(response).path("token").asText();
    }
}
