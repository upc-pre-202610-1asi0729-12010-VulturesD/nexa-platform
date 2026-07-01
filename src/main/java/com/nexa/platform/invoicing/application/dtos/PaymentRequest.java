package com.nexa.platform.invoicing.application.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PaymentRequest(@NotNull Long invoiceId, Long clientAccountId, Long paymentMethodRecordId,
                             @NotNull @DecimalMin("0.01") BigDecimal amount, String currency,
                             @NotBlank String method, String referenceCode) { }
