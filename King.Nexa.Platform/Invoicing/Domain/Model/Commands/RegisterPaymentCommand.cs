using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Commands;

public record RegisterPaymentCommand(int InvoiceId, BillingAmount BillingAmount, string ReferenceCode);
