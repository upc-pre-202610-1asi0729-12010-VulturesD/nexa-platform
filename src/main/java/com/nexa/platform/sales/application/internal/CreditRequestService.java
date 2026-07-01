package com.nexa.platform.sales.application.internal;

import com.nexa.platform.sales.application.dtos.CreditRequestDtos;
import com.nexa.platform.sales.domain.model.CreditRequest;
import com.nexa.platform.sales.domain.model.Customer;
import com.nexa.platform.sales.domain.model.repositories.CreditRequestRepositoryPort;
import com.nexa.platform.sales.domain.model.repositories.CustomerRepositoryPort;
import com.nexa.platform.shared.application.security.WorkspaceScopeException;
import com.nexa.platform.shared.application.auditing.AuditLogService;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditRequestService {
    private final CreditRequestRepositoryPort requests;
    private final CustomerRepositoryPort customers;
    private final AuditLogService auditLog;

    public CreditRequestService(CreditRequestRepositoryPort requests, CustomerRepositoryPort customers,
                                AuditLogService auditLog) {
        this.requests = requests;
        this.customers = customers;
        this.auditLog = auditLog;
    }

    @Transactional(readOnly = true)
    public List<CreditRequestDtos.CreditRequestResponse> list(Long tenantId, Long clientAccountId) {
        requireTenant(tenantId);
        List<CreditRequest> rows = clientAccountId == null
            ? requests.findByTenantIdOrderByIdDesc(tenantId)
            : requests.findByTenantIdAndClientAccountIdOrderByIdDesc(tenantId, clientAccountId);
        return rows.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CreditRequestDtos.CreditRequestResponse get(Long tenantId, Long clientAccountId, Long id) {
        return toResponse(find(tenantId, clientAccountId, id));
    }

    @Transactional
    public CreditRequestDtos.CreditRequestResponse create(Long tenantId, Long sessionClientAccountId,
                                                          Long userId,
                                                          CreditRequestDtos.CreateCreditRequestRequest request) {
        requireTenant(tenantId);
        Long clientAccountId = request.clientAccountId();
        if (sessionClientAccountId != null) {
            if (clientAccountId != null && !sessionClientAccountId.equals(clientAccountId)) {
                throw new WorkspaceScopeException("Client account does not match authenticated buyer session.");
            }
            clientAccountId = sessionClientAccountId;
        }
        if (clientAccountId == null || clientAccountId <= 0) {
            throw new IllegalArgumentException("Client account is required.");
        }
        Customer client = customers.findByIdAndTenantId(clientAccountId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Client account does not belong to the current tenant."));
        if (request.code() != null && !request.code().isBlank()
            && requests.findByTenantIdAndCode(tenantId, request.code().trim().toUpperCase()).isPresent()) {
            throw new IllegalArgumentException("Credit request code already exists.");
        }
        CreditRequest saved = requests.save(new CreditRequest(tenantId, client, request.code(),
            request.requestedAmount(), request.reason(), userId));
        auditLog.record(tenantId, "credit_request.created", "credit_request", saved.getId(), null);
        return toResponse(saved);
    }

    @Transactional
    public CreditRequestDtos.CreditRequestResponse resolve(Long tenantId, Long id,
                                                           CreditRequestDtos.ResolveCreditRequestRequest request) {
        CreditRequest creditRequest = find(tenantId, null, id);
        creditRequest.resolve(request.status(), request.reviewedBy(), request.note());
        CreditRequest saved = requests.save(creditRequest);
        auditLog.record(tenantId, "credit_request.resolved", "credit_request", saved.getId(),
            "{\"status\":\"" + saved.getStatus() + "\"}");
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long tenantId, Long id) {
        CreditRequest request = find(tenantId, null, id);
        requests.delete(request);
        auditLog.record(tenantId, "credit_request.deleted", "credit_request", id, null);
    }

    private CreditRequest find(Long tenantId, Long clientAccountId, Long id) {
        requireTenant(tenantId);
        return (clientAccountId == null
            ? requests.findByIdAndTenantId(id, tenantId)
            : requests.findByIdAndTenantIdAndClientAccountId(id, tenantId, clientAccountId))
            .orElseThrow(() -> new ResourceNotFoundException("Credit request", id));
    }

    private CreditRequestDtos.CreditRequestResponse toResponse(CreditRequest request) {
        return new CreditRequestDtos.CreditRequestResponse(request.getId(), request.getTenantId(),
            request.getClientAccount().getId(), request.getCode(), request.getRequestedAmount(),
            request.getReason(), request.getStatus(), request.getCreatedByUserId(), request.getReviewedBy(),
            request.getResolutionNote(), request.getCreatedAt(), request.getUpdatedAt());
    }

    private static void requireTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) throw new IllegalArgumentException("Current tenant is required.");
    }
}
