package com.nexa.platform.sales.application.dtos;

import java.math.BigDecimal;

public record UpsertCustomerRequest(String code, String businessName, String commercialName, String taxId,
                                    String segment, String contact, String contactEmail, String phone,
                                    String deliveryAddress, String district, String province,
                                    String deliveryReference, String documentProfile, String paymentCondition,
                                    BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed,
                                    String monthlyCreditStatus, String deliveryPreference, boolean portalAccess,
                                    String sellerWorkspaceEmail, String status) { }
