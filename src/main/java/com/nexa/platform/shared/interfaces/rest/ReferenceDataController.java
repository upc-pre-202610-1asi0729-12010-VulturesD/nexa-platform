package com.nexa.platform.shared.interfaces.rest;

import com.nexa.platform.shared.domain.repositories.DocumentTypeRepositoryPort;
import com.nexa.platform.shared.interfaces.rest.resources.ReferenceOptionResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/reference", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Reference Data", description = "Workspace reference catalogs")
@PreAuthorize("isAuthenticated()")
public class ReferenceDataController {
    private final DocumentTypeRepositoryPort documentTypes;

    public ReferenceDataController(DocumentTypeRepositoryPort documentTypes) {
        this.documentTypes = documentTypes;
    }

    @Operation(summary = "List active business document types")
    @GetMapping("/document-types")
    public List<ReferenceOptionResource> documentTypes() {
        return documentTypes.findAllByOrderByIdAsc().stream()
            .filter(type -> type.isActive())
            .map(type -> new ReferenceOptionResource(type.getId(), type.getKey(), type.getLabel(), null, true))
            .toList();
    }

    @Operation(summary = "List countries")
    @GetMapping("/countries")
    public List<ReferenceOptionResource> countries() {
        return List.of(option(1L, "PE", "Peru"));
    }

    @Operation(summary = "List departments")
    @GetMapping("/departments")
    public List<ReferenceOptionResource> departments() {
        return List.of(option(1L, "lima", "Lima", "PE"));
    }

    @Operation(summary = "List provinces")
    @GetMapping("/provinces")
    public List<ReferenceOptionResource> provinces() {
        return List.of(
            option(1L, "lima", "Lima Metropolitana", "lima"),
            option(2L, "callao", "Callao", "lima"));
    }

    @Operation(summary = "List districts")
    @GetMapping("/districts")
    public List<ReferenceOptionResource> districts() {
        return Arrays.asList(
            option(1L, "miraflores", "Miraflores", "lima"),
            option(2L, "san-isidro", "San Isidro", "lima"),
            option(3L, "san-borja", "San Borja", "lima"),
            option(4L, "surco", "Surco", "lima"),
            option(5L, "santiago-de-surco", "Santiago de Surco", "lima"),
            option(6L, "la-molina", "La Molina", "lima"),
            option(7L, "barranco", "Barranco", "lima"),
            option(8L, "surquillo", "Surquillo", "lima"),
            option(9L, "lince", "Lince", "lima"),
            option(10L, "jesus-maria", "Jesus Maria", "lima"),
            option(11L, "magdalena-del-mar", "Magdalena del Mar", "lima"),
            option(12L, "pueblo-libre", "Pueblo Libre", "lima"),
            option(13L, "san-miguel", "San Miguel", "lima"),
            option(14L, "cercado-de-lima", "Cercado de Lima", "lima"),
            option(15L, "la-victoria", "La Victoria", "lima"),
            option(16L, "san-luis", "San Luis", "lima"),
            option(17L, "ate", "Ate", "lima"),
            option(18L, "santa-anita", "Santa Anita", "lima"),
            option(19L, "san-juan-de-lurigancho", "San Juan de Lurigancho", "lima"),
            option(20L, "san-juan-de-miraflores", "San Juan de Miraflores", "lima"),
            option(21L, "villa-el-salvador", "Villa El Salvador", "lima"),
            option(22L, "villa-maria-del-triunfo", "Villa Maria del Triunfo", "lima"),
            option(23L, "los-olivos", "Los Olivos", "lima"),
            option(24L, "independencia", "Independencia", "lima"),
            option(25L, "comas", "Comas", "lima"),
            option(26L, "carabayllo", "Carabayllo", "lima"),
            option(27L, "puente-piedra", "Puente Piedra", "lima"),
            option(28L, "chorrillos", "Chorrillos", "lima"),
            option(29L, "callao-district", "Callao", "callao"),
            option(30L, "bellavista", "Bellavista", "callao"),
            option(31L, "carmen-de-la-legua-reynoso", "Carmen de la Legua Reynoso", "callao"),
            option(32L, "la-perla", "La Perla", "callao"),
            option(33L, "la-punta", "La Punta", "callao"),
            option(34L, "ventanilla", "Ventanilla", "callao"),
            option(35L, "mi-peru", "Mi Peru", "callao"));
    }

    @Operation(summary = "List payment options")
    @GetMapping("/payment-options")
    public List<ReferenceOptionResource> paymentOptions() {
        return List.of(
            option(1L, "credit_line", "B2B credit line"),
            option(2L, "bank_transfer", "Bank transfer"),
            option(3L, "cash", "Cash before dispatch"),
            option(4L, "cash_on_delivery", "Cash on delivery"));
    }

    @Operation(summary = "List delivery methods")
    @GetMapping("/delivery-methods")
    public List<ReferenceOptionResource> deliveryMethods() {
        return List.of(
            option(1L, "scheduled_route", "Scheduled cold route"),
            option(2L, "buyer_pickup", "Buyer pickup"),
            option(3L, "third_party_cold_carrier", "Third-party cold carrier"));
    }

    @Operation(summary = "List units of measure")
    @GetMapping("/units-of-measure")
    public List<ReferenceOptionResource> unitsOfMeasure() {
        return List.of(
            option(1L, "box", "Box"),
            option(2L, "kg", "Kilogram"),
            option(3L, "unit", "Unit"),
            option(4L, "pack", "Pack"));
    }

    @Operation(summary = "List operational statuses")
    @GetMapping("/statuses")
    public StatusReferenceResource statuses() {
        return new StatusReferenceResource(
            List.of("submitted", "buyer_adjustment_requested", "commercially_validated", "rejected", "cancelled", "converted_to_order"),
            List.of("ready_for_operations", "assigned", "scheduled", "in_route", "delivered", "incident", "reprogrammed"),
            List.of("pending", "confirmed", "rejected", "cancelled", "paid"),
            List.of("pending", "paid", "cancelled"),
            List.of("pending", "confirmed", "failed", "rejected", "cancelled"),
            List.of("pending", "uploaded", "ready", "missing", "accepted"));
    }

    private static ReferenceOptionResource option(Long id, String code, String label) {
        return option(id, code, label, null);
    }

    private static ReferenceOptionResource option(Long id, String code, String label, String parentCode) {
        return new ReferenceOptionResource(id, code, label, parentCode, true);
    }

    public record StatusReferenceResource(
        List<String> purchaseRequests,
        List<String> dispatchOrders,
        List<String> orders,
        List<String> invoices,
        List<String> payments,
        List<String> documents) { }
}
