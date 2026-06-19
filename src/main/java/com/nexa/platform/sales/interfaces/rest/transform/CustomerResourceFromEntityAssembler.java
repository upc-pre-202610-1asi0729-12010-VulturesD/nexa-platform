package com.nexa.platform.sales.interfaces.rest.transform;

import com.nexa.platform.sales.application.dtos.CustomerResponse;
import com.nexa.platform.sales.interfaces.rest.resources.CustomerResource;

public final class CustomerResourceFromEntityAssembler {
    private CustomerResourceFromEntityAssembler() { }

    public static CustomerResource toResourceFromEntity(CustomerResponse response) {
        return new CustomerResource(response.id(), response.businessName(), response.taxId(),
            response.contactEmail(), response.deliveryAddress());
    }
}
