package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.CustomerResponse;
import com.nexa.platform.sales.application.dtos.UpsertCustomerRequest;
import com.nexa.platform.sales.interfaces.rest.resources.CustomerResource;
import com.nexa.platform.sales.interfaces.rest.resources.UpsertCustomerResource;

public final class CustomerResourceFromEntityAssembler {
    private CustomerResourceFromEntityAssembler() { }

    public static CustomerResource toResourceFromEntity(CustomerResponse response) {
        return new CustomerResource(response.id(), response.tenantId(), response.code(), response.businessName(),
            response.commercialName(), response.taxId(), response.taxId(), response.segment(), response.contact(),
            response.contactEmail(), response.phone(), response.deliveryAddress(), response.deliveryAddress(),
            response.district(), response.province(), response.deliveryReference(), response.documentProfile(),
            response.paymentCondition(), response.monthlyCreditLimit(), response.monthlyCreditUsed(),
            response.monthlyCreditAvailable(), response.monthlyCreditStatus(), response.deliveryPreference(),
            response.portalAccess(), response.sellerWorkspaceEmail(), response.status());
    }

    public static UpsertCustomerRequest toRequestFromResource(UpsertCustomerResource resource) {
        return new UpsertCustomerRequest(resource.code(), resource.businessName(), resource.commercialName(),
            resource.taxId(), resource.segment(), resource.contact(), resource.contactEmail(), resource.phone(),
            resource.deliveryAddress(), resource.district(), resource.province(), resource.deliveryReference(),
            resource.documentProfile(), resource.paymentCondition(), resource.monthlyCreditLimit(),
            resource.monthlyCreditUsed(), resource.monthlyCreditStatus(), resource.deliveryPreference(),
            resource.portalAccess(), resource.sellerWorkspaceEmail(), resource.status());
    }
}
