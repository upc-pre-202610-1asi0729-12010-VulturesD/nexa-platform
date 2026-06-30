package com.nexa.platform.sales.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record UpsertCustomerResource(String code, @NotBlank String businessName, String commercialName,
                                     @NotBlank String taxId, String segment, String contact, String contactEmail,
                                     String phone, String deliveryAddress, String district, String province,
                                     String deliveryReference, String documentProfile, String paymentCondition,
                                     BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed,
                                     String monthlyCreditStatus, String deliveryPreference, boolean portalAccess,
                                     String sellerWorkspaceEmail, String status) { }
