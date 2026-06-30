package com.nexa.platform.sales.interfaces.rest.resources;

import java.math.BigDecimal;

public record ClientFinancialProfileResource(Long id, Long tenantId, String code, String paymentCondition,
                                             BigDecimal monthlyCreditLimit, BigDecimal monthlyCreditUsed,
                                             BigDecimal monthlyCreditAvailable, String monthlyCreditStatus,
                                             String status) { }
