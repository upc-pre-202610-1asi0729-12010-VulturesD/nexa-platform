package com.nexa.platform.logistics.interfaces.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LogisticsOperationalRecordsTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void operationalRecordsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/dispatch-events"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "LOGISTICS")
    void dispatchOperationalRecordsMatchAppsWebContract() throws Exception {
        mockMvc.perform(get("/api/v1/dispatch-orders/1/events"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("ready_for_operations")));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "status": "in_route",
                      "description": "Cold route departed Lima hub.",
                      "visibleToBuyer": true
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dispatchOrderId").value(1))
            .andExpect(jsonPath("$.status").value("in_route"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/temperature-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "celsius": 4.20,
                      "zone": "truck-cabin",
                      "status": "alert",
                      "recordedAt": "2026-06-13T14:05:00-05:00"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dispatchOrderId").value(1))
            .andExpect(jsonPath("$.status").value("alert"));

        mockMvc.perform(post("/api/v1/temperature-logs/1/resolve-alert"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("resolved"));

        mockMvc.perform(post("/api/v1/dispatch-orders/1/proofs-of-delivery")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "receivedBy": "Elena Litano",
                      "completedAt": "2026-06-13T17:45:00-05:00",
                      "photoReference": true,
                      "signatureReference": true,
                      "notes": "Received sealed chilled boxes."
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dispatchOrderId").value(1))
            .andExpect(jsonPath("$.status").value("completed"))
            .andExpect(jsonPath("$.receivedBy").value("Elena Litano"));
    }
}
