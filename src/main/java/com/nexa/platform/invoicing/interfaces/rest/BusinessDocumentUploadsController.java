package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.dtos.BusinessDocumentDtos.UploadBusinessDocumentRequest;
import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentA1Resources.BusinessDocumentA1Resource;
import com.nexa.platform.invoicing.interfaces.rest.transform.BusinessDocumentA1ResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/business-document-uploads")
@Tag(name = "Business Document Uploads", description = "Upload tenant-scoped business document content")
@PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
public class BusinessDocumentUploadsController {
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;

    public BusinessDocumentUploadsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "Upload a business document")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessDocumentA1Resource> create(
        @RequestParam Long tenantId,
        @RequestParam(required = false) Long orderId,
        @RequestParam(required = false) Long clientAccountId,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String label,
        @RequestParam(defaultValue = "false") boolean visibleToBuyer,
        @RequestParam(defaultValue = "false") boolean required,
        @RequestPart("file") MultipartFile file) {
        Long scopedTenantId = workspace.requireTenant(tenantId);
        var uploaded = service.uploadBusinessDocument(scopedTenantId, new UploadBusinessDocumentRequest(
            tenantId, orderId, clientAccountId, type, label, visibleToBuyer, required,
            file.getOriginalFilename(), file.getContentType(), content(file)));
        var resource = BusinessDocumentA1ResourceAssembler.toResource(uploaded);
        return ResponseEntity.created(URI.create("/api/v1/business-documents/" + resource.id())).body(resource);
    }

    private static byte[] content(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Document file could not be read.", exception);
        }
    }
}
