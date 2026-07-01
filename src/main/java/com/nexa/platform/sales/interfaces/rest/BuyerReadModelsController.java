package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService.BuyerDashboardSummaryReadModel;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService.ClientFinancialProfileReadModel;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService.OrderLifecycleReadModel;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import com.nexa.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/buyer")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Buyer read models", description = "Buyer portal workspace projections")
public class BuyerReadModelsController {
    private final SalesBuyerReadModelService readModels;
    private final CurrentWorkspaceContext workspace;

    public BuyerReadModelsController(SalesBuyerReadModelService readModels, CurrentWorkspaceContext workspace) {
        this.readModels = readModels;
        this.workspace = workspace;
    }

    @GetMapping("/dashboard-summary")
    public BuyerDashboardSummaryReadModel dashboardSummary() {
        return readModels.buyerDashboard(workspace.requireTenant(null), workspace.clientAccountId());
    }

    @GetMapping("/orders/{id}/lifecycle")
    public OrderLifecycleReadModel orderLifecycle(@PathVariable Long id) {
        OrderLifecycleReadModel lifecycle = readModels.buyerOrderLifecycle(
            workspace.requireTenant(null), workspace.clientAccountId(), id);
        if (lifecycle == null) throw new ResourceNotFoundException("Order", id);
        return lifecycle;
    }

    @GetMapping("/financial-profile")
    public ClientFinancialProfileReadModel financialProfile() {
        return readModels.buyerFinancialProfile(workspace.requireTenant(null), workspace.clientAccountId());
    }
}
