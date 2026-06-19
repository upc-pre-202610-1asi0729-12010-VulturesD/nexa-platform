package com.nexa.platform.invoicing.interfaces.rest;

import com.nexa.platform.invoicing.application.internal.InvoicingService;
import com.nexa.platform.invoicing.interfaces.rest.resources.BusinessDocumentResource;
import com.nexa.platform.invoicing.interfaces.rest.transform.BusinessDocumentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Inbound REST resource for business documents.
 *
 * <p>Business documents are invoice-backed documents visible to both the
 * commercial (Valeria) and logistics (Roberto) portals. The endpoint mirrors
 * the {@code /api/v1/business-documents} contract used by the Open Source WebApp.
 *
 * @since 1.0
 */
@RestController
@RequestMapping(value = {"/api/v1/business-documents", "/api/v1/documents"}, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Business Documents", description = "Invoice and business document endpoints for commercial and logistics portals")
public class BusinessDocumentsController {

    private final InvoicingService service;

    public BusinessDocumentsController(InvoicingService service) {
        this.service = service;
    }

    @Operation(
        summary = "List all business documents",
        description = "Returns all invoice-backed business documents. Accessible by commercial and logistics roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Business document list returned")
    })
    @GetMapping
    public List<BusinessDocumentResource> list() {
        return service.listBusinessDocuments().stream()
            .map(BusinessDocumentResourceFromEntityAssembler::toResourceFromEntity)
            .toList();
    }
}
