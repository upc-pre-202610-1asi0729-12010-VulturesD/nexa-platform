package com.nexa.platform.sales.interfaces.rest;

import com.nexa.platform.shared.application.pagination.PagedResult;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService.OrderSummaryReadModel;
import com.nexa.platform.shared.application.readmodels.SalesBuyerReadModelService.PurchaseRequestInboxReadModel;
import com.nexa.platform.shared.application.security.CurrentWorkspaceContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
@PreAuthorize("hasAnyRole('ADMIN','SALES')")
@Tag(name = "Sales read models", description = "Commercial workspace projections")
public class SalesReadModelsController {
    private final SalesBuyerReadModelService readModels;
    private final CurrentWorkspaceContext workspace;

    public SalesReadModelsController(SalesBuyerReadModelService readModels, CurrentWorkspaceContext workspace) {
        this.readModels = readModels;
        this.workspace = workspace;
    }

    @GetMapping("/order-summaries")
    public PagedResult<OrderSummaryReadModel> orderSummaries(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize) {
        return readModels.salesOrderSummaries(workspace.requireTenant(null), page, pageSize);
    }

    @GetMapping("/purchase-request-inbox")
    public PagedResult<PurchaseRequestInboxReadModel> purchaseRequestInbox(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize) {
        return readModels.purchaseRequestInbox(workspace.requireTenant(null), page, pageSize);
    }
}
