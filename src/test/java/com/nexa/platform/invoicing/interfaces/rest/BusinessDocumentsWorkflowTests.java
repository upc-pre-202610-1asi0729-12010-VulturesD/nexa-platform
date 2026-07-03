package com.nexa.platform.invoicing.interfaces.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BusinessDocumentsWorkflowTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void businessDocumentsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/business-documents"))
            .andExpect(status().isForbidden());
    }

    @Test
    void businessDocumentLifecycleMatchesAppsWebContract() throws Exception {
        String authorization = authorization("sales@nexa.com");
        mockMvc.perform(get("/api/v1/business-documents").header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].tenantId").value(1))
            .andExpect(jsonPath("$[0].type").value("factura_pdf"))
            .andExpect(jsonPath("$[0].status").value("ready"));

        mockMvc.perform(get("/api/v1/business-documents/1/content").header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/pdf"))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
            .andExpect(content().string(startsWith("%PDF-1.4")));

        String generatedResponse = mockMvc.perform(post("/api/v1/business-documents/generations")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "tenantId": 1,
                      "orderId": 1,
                      "type": "factura_xml"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("factura_xml"))
            .andExpect(jsonPath("$.status").value("ready"))
            .andExpect(jsonPath("$.visibleToBuyer").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long generatedId = objectMapper.readTree(generatedResponse).get("id").asLong();
        mockMvc.perform(get("/api/v1/business-documents/{id}/content", generatedId)
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
            .andExpect(content().string(containsString("<Invoice")));

        mockMvc.perform(post("/api/v1/business-documents/1/status-changes")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "status": "accepted", "visibleToBuyer": true }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("accepted"));

        mockMvc.perform(post("/api/v1/business-documents/1/status-changes")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "status": "pending" }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void uploadedBusinessDocumentCanBeDownloaded() throws Exception {
        String authorization = authorization("sales@nexa.com");
        MockMultipartFile file = new MockMultipartFile(
            "file", "../../evidence.txt", MediaType.TEXT_PLAIN_VALUE, "delivery evidence".getBytes());

        String response = mockMvc.perform(multipart("/api/v1/business-document-uploads")
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .param("tenantId", "1")
                .param("orderId", "1")
                .param("clientAccountId", "1")
                .param("type", "business_document")
                .param("visibleToBuyer", "true")
                .param("required", "true"))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, startsWith("/api/v1/business-documents/")))
            .andExpect(jsonPath("$.status").value("uploaded"))
            .andExpect(jsonPath("$.fileName").value("evidence.txt"))
            .andExpect(jsonPath("$.label").value("Business document"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();
        mockMvc.perform(get("/api/v1/business-documents/{id}/content", id)
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("delivery evidence"));
    }

    @Test
    void emptyBusinessDocumentUploadIsRejected() throws Exception {
        String authorization = authorization("sales@nexa.com");
        MockMultipartFile file = new MockMultipartFile(
            "file", "empty.pdf", MediaType.APPLICATION_PDF_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/v1/business-document-uploads")
                .file(file)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .param("tenantId", "1")
                .param("type", "business_document"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void buyerReadsOwnVisibleDocumentsButCannotGenerate() throws Exception {
        String authorization = authorization("buyer@nexa.com");
        mockMvc.perform(get("/api/v1/business-documents")
                .header(HttpHeaders.AUTHORIZATION, authorization))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clientAccountId").value(1))
            .andExpect(jsonPath("$[0].visibleToBuyer").value(true));

        mockMvc.perform(post("/api/v1/business-documents/generations")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orderId\":1,\"type\":\"factura_pdf\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void businessDocumentsRejectWrongTenantHeader() throws Exception {
        mockMvc.perform(get("/api/v1/business-documents")
                .header(HttpHeaders.AUTHORIZATION, authorization("sales@nexa.com"))
                .header("X-Nexa-Tenant-Id", "2"))
            .andExpect(status().isForbidden());
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
