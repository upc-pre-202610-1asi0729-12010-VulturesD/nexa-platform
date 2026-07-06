package com.nexa.platform.logistics.interfaces.rest;

import com.nexa.platform.logistics.application.internal.LogisticsService;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.CompletePodResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.ProofOfDeliveryRecordResource;
import com.nexa.platform.logistics.interfaces.rest.resources.DispatchOrderResources.UpsertProofOfDeliveryRecordResource;
import com.nexa.platform.logistics.interfaces.rest.transform.DispatchOrderResourceAssembler;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
@Tag(name = "Proof Of Delivery Records", description = "Cold-chain dispatch delivery evidence")
@PreAuthorize("hasAnyRole('ADMIN','LOGISTICS')")
public class ProofOfDeliveryRecordsController {
    private final LogisticsService service;
    private final CurrentWorkspaceContext workspace;

    public ProofOfDeliveryRecordsController(LogisticsService service, CurrentWorkspaceContext workspace) {
        this.service = service;
        this.workspace = workspace;
    }

    @Operation(summary = "List proof of delivery records")
    @GetMapping("/api/v1/proof-of-delivery-records")
    public List<ProofOfDeliveryRecordResource> list() {
        Long tenantId = workspace.requireTenant(null);
        return service.listProofsOfDelivery().stream().filter(row -> tenantId.equals(row.tenantId()))
            .map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "List proof of delivery records by dispatch order")
    @GetMapping("/api/v1/dispatch-orders/{dispatchOrderId}/proofs-of-delivery")
    public List<ProofOfDeliveryRecordResource> listByDispatch(@PathVariable Long dispatchOrderId) {
        scopeDispatch(dispatchOrderId);
        return service.listProofsOfDeliveryByDispatch(dispatchOrderId).stream().map(DispatchOrderResourceAssembler::toResource).toList();
    }

    @Operation(summary = "Get proof of delivery record")
    @GetMapping("/api/v1/proof-of-delivery-records/{id}")
    public ProofOfDeliveryRecordResource get(@PathVariable Long id) {
        return scoped(DispatchOrderResourceAssembler.toResource(service.getProofOfDelivery(id)));
    }

    @Operation(summary = "Create proof of delivery record")
    @PostMapping("/api/v1/proof-of-delivery-records")
    @ResponseStatus(HttpStatus.CREATED)
    public ProofOfDeliveryRecordResource create(@Valid @RequestBody UpsertProofOfDeliveryRecordResource resource) {
        workspace.requireTenant(resource.tenantId());
        scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(service.createProofOfDelivery(DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Create proof of delivery for dispatch order")
    @PostMapping("/api/v1/dispatch-orders/{dispatchOrderId}/proofs-of-delivery")
    @ResponseStatus(HttpStatus.CREATED)
    public ProofOfDeliveryRecordResource createForDispatch(@PathVariable Long dispatchOrderId,
                                                           @Valid @RequestBody CompletePodResource resource) {
        scopeDispatch(dispatchOrderId);
        return DispatchOrderResourceAssembler.toResource(
            service.createProofOfDeliveryForDispatch(dispatchOrderId, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Update proof of delivery record")
    @PutMapping("/api/v1/proof-of-delivery-records/{id}")
    public ProofOfDeliveryRecordResource update(@PathVariable Long id,
                                                @Valid @RequestBody UpsertProofOfDeliveryRecordResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getProofOfDelivery(id)));
        workspace.requireTenant(resource.tenantId());
        scopeDispatch(resource.dispatchOrderId());
        return DispatchOrderResourceAssembler.toResource(
            service.updateProofOfDelivery(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Complete proof of delivery record")
    @PostMapping("/api/v1/proof-of-delivery-records/{id}/completion")
    public ProofOfDeliveryRecordResource complete(@PathVariable Long id, @Valid @RequestBody CompletePodResource resource) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getProofOfDelivery(id)));
        return DispatchOrderResourceAssembler.toResource(
            service.completeProofOfDelivery(id, DispatchOrderResourceAssembler.toRequest(resource)));
    }

    @Operation(summary = "Delete proof of delivery record")
    @DeleteMapping("/api/v1/proof-of-delivery-records/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        scoped(DispatchOrderResourceAssembler.toResource(service.getProofOfDelivery(id)));
        service.deleteProofOfDelivery(id);
    }

    private ProofOfDeliveryRecordResource scoped(ProofOfDeliveryRecordResource resource) {
        workspace.requireTenant(resource.tenantId());
        return resource;
    }

    private void scopeDispatch(Long dispatchOrderId) {
        workspace.requireTenant(service.getDispatchOrder(dispatchOrderId).tenantId());
    }
}
