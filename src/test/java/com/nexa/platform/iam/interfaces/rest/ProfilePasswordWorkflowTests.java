package com.nexa.platform.iam.interfaces.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexa.platform.iam.domain.model.UserAccount;
import com.nexa.platform.iam.domain.model.repositories.UserAccountRepositoryPort;
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
class ProfilePasswordWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserAccountRepositoryPort users;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordChangeRequiresAuthenticatedWorkspace() throws Exception {
        mockMvc.perform(post("/api/v1/profile/password-changes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validChange("NexaAccess2026!", "ChangedAccess2026!", "ChangedAccess2026!")))
            .andExpect(status().isForbidden());
    }

    @Test
    void passwordChangeRejectsInvalidCurrentConfirmationPolicyAndReuse() throws Exception {
        String token = login("sales@nexa.com", "NexaAccess2026!");

        change(token, "wrong-password", "ChangedAccess2026!", "ChangedAccess2026!")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Current password is incorrect."));

        change(token, "NexaAccess2026!", "ChangedAccess2026!", "different")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("Password confirmation does not match."));

        change(token, "NexaAccess2026!", "weakpassword", "weakpassword")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value(
                "Password must contain at least 10 characters, uppercase, lowercase, number, and symbol."));

        change(token, "NexaAccess2026!", "NexaAccess2026!", "NexaAccess2026!")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details[0]").value("New password must differ from the current password."));
    }

    @Test
    void passwordChangePersistsNewHashSupportsLoginAndWritesAudit() throws Exception {
        String token = login("sales@nexa.com", "NexaAccess2026!");

        change(token, "NexaAccess2026!", "ChangedAccess2026!", "ChangedAccess2026!")
            .andExpect(status().isNoContent());

        UserAccount account = users.findByEmail("sales@nexa.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertFalse(passwordEncoder.matches("NexaAccess2026!", account.getPasswordHash()));
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("ChangedAccess2026!", account.getPasswordHash()));

        login("sales@nexa.com", "ChangedAccess2026!");

        mockMvc.perform(get("/api/v1/audit-logs").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].action").value("profile.password_changed"))
            .andExpect(jsonPath("$[0].resourceType").value("UserAccount"));
    }

    @Test
    void currentProfileUpdatePersistsAppsWebFields() throws Exception {
        String token = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of(
                    "fullName", "Valeria Sanchez Updated",
                    "email", "sales@nexa.com",
                    "phone", "+51 999 123 456",
                    "preferredLanguage", "es",
                    "criticalNotificationsEnabled", false))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Valeria Sanchez Updated"))
            .andExpect(jsonPath("$.phone").value("+51 999 123 456"))
            .andExpect(jsonPath("$.preferredLanguage").value("es"))
            .andExpect(jsonPath("$.criticalNotificationsEnabled").value(false));

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Valeria Sanchez Updated"))
            .andExpect(jsonPath("$.phone").value("+51 999 123 456"))
            .andExpect(jsonPath("$.preferredLanguage").value("es"))
            .andExpect(jsonPath("$.criticalNotificationsEnabled").value(false));
    }

    @Test
    void currentProfileUpdateRejectsInvalidLanguage() throws Exception {
        String token = login("sales@nexa.com", "NexaAccess2026!");

        mockMvc.perform(put("/api/v1/users/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of(
                    "fullName", "Valeria Sanchez",
                    "email", "sales@nexa.com",
                    "phone", "",
                    "preferredLanguage", "fr",
                    "criticalNotificationsEnabled", true))))
            .andExpect(status().isBadRequest());
    }

    private org.springframework.test.web.servlet.ResultActions change(
        String token, String currentPassword, String newPassword, String confirmPassword) throws Exception {
        return mockMvc.perform(post("/api/v1/profile/password-changes")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(validChange(currentPassword, newPassword, confirmPassword)));
    }

    private String validChange(String currentPassword, String newPassword, String confirmPassword) throws Exception {
        return objectMapper.writeValueAsString(java.util.Map.of(
            "currentPassword", currentPassword,
            "newPassword", newPassword,
            "confirmPassword", confirmPassword));
    }

    private String login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(java.util.Map.of("email", email, "password", password))))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).path("token").asText();
    }
}
