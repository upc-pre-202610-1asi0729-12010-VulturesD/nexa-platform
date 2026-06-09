namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

public record RegisterPaymentResource(int InvoiceId, decimal Amount, string Currency, string ReferenceCode);
