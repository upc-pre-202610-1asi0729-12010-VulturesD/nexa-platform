package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentA1Resources.*;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.BusinessDocumentA1ResourceAssembler;
import com.nexa.platform.invoicing.interfaces.rest.transform.BusinessDocumentResourceFromEntityAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@Tag(name = "Business Documents", description = "Tenant-scoped business document lifecycle and downloadable content")
@PreAuthorize("isAuthenticated()")
public class BusinessDocumentsController {
    private static final String TENANT_HEADER = "X-Nexa-Tenant-Id";
    private final InvoicingService service;
    private final CurrentWorkspaceContext workspace;

    public BusinessDocumentsController(InvoicingService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List business documents")
    @GetMapping("/api/v1/business-documents")
    public List<BusinessDocumentA1Resource> list(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        Authentication authentication) {
        tenantId = workspace.requireTenant(tenantId);
        return service.listBusinessDocuments(tenantId).stream().map(BusinessDocumentA1ResourceAssembler::toResource)
            .filter(resource -> canRead(resource, authentication)).toList();
    }

    @Operation(summary = "List business documents using the current Angular compatibility shape")
    @GetMapping("/api/v1/documents")
    public List<BusinessDocumentResource> listCompatibility(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        Authentication authentication) {
        tenantId = workspace.requireTenant(tenantId);
        String buyerClientId = compatibilityClientId(workspace.clientAccountId());
        return service.listBusinessDocumentCompatibility(tenantId).stream()
            .map(BusinessDocumentResourceFromEntityAssembler::toResourceFromEntity)
            .filter(resource -> !isBuyer(authentication)
                || resource.visibleToBuyer() && buyerClientId.equals(resource.clientId()))
            .toList();
    }

    @Operation(summary = "Get business document")
    @GetMapping("/api/v1/business-documents/{id}")
    public BusinessDocumentA1Resource get(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, Authentication authentication) {
        BusinessDocumentA1Resource resource = BusinessDocumentA1ResourceAssembler.toResource(
            service.getBusinessDocument(workspace.requireTenant(tenantId), id));
        requireRead(resource, authentication);
        return resource;
    }

    @Operation(summary = "Download business document content")
    @GetMapping(value = "/api/v1/business-documents/{id}/content", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> content(
        @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
        @PathVariable Long id, Authentication authentication) {
        tenantId = workspace.requireTenant(tenantId);
        BusinessDocumentA1Resource resource = BusinessDocumentA1ResourceAssembler.toResource(
            service.getBusinessDocument(tenantId, id));
        requireRead(resource, authentication);
        var content = service.getBusinessDocumentContent(tenantId, id);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(content.contentType());
        } catch (InvalidMediaTypeException exception) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(content.fileName(), StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .body(content.content());
    }

    @Operation(summary = "Create business document")
    @PostMapping("/api/v1/business-documents")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessDocumentA1Resource create(@Valid @RequestBody CreateBusinessDocumentResource resource) {
        Long tenantId = workspace.requireTenant(resource.tenantId());
        return BusinessDocumentA1ResourceAssembler.toResource(
            service.createBusinessDocument(tenantId, BusinessDocumentA1ResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Generate business document content from an order")
    @PostMapping("/api/v1/business-documents/generations")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessDocumentA1Resource generate(@Valid @RequestBody GenerateBusinessDocumentResource resource) {
        Long tenantId = workspace.requireTenant(resource.tenantId());
        return BusinessDocumentA1ResourceAssembler.toResource(
            service.generateBusinessDocument(tenantId, BusinessDocumentA1ResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Change business document status")
    @PostMapping("/api/v1/business-documents/{id}/status-changes")
    @PreAuthorize("hasAnyRole('ADMIN','SALES','LOGISTICS')")
    public BusinessDocumentA1Resource changeStatus(@PathVariable Long id,
                                                   @RequestHeader(value = TENANT_HEADER, required = false) Long tenantId,
                                                   @Valid @RequestBody ChangeBusinessDocumentStatusResource resource) {
        return BusinessDocumentA1ResourceAssembler.toResource(service.changeBusinessDocumentStatus(
            workspace.requireTenant(tenantId), id, BusinessDocumentA1ResourceAssembler.toRequest(resource)));
    }

    private boolean canRead(BusinessDocumentA1Resource resource, Authentication authentication) {
        return !isBuyer(authentication) || resource.visibleToBuyer()
            && resource.clientAccountId() != null && resource.clientAccountId().equals(workspace.clientAccountId());
    }

    private void requireRead(BusinessDocumentA1Resource resource, Authentication authentication) {
        if (!canRead(resource, authentication)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private boolean isBuyer(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_BUYER".equals(authority.getAuthority()));
    }

    private String compatibilityClientId(Long clientAccountId) {
        return clientAccountId == null ? "" : "CLI-" + String.format("%03d", clientAccountId);
    }
}
