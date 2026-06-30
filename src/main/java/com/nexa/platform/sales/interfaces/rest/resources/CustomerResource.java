package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;

public record CustomerResource(Long id, Long tenantId, String code, String businessName, String commercialName,
                               String taxId, String ruc, String segment, String contact, String contactEmail,
                               String phone, String deliveryAddress, String address, String district, String province,
                               String deliveryReference, String documentProfile, String paymentCondition,
                               BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed,
                               BigDecimal monthlyCreditAvailable, String monthlyCreditStatus,
                               String deliveryPreference, boolean portalAccess, String sellerWorkspaceEmail,
                               String status) { }
