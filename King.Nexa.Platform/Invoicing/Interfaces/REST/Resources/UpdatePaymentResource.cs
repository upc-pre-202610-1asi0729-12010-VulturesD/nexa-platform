namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

public record UpdatePaymentResource(int InvoiceId, decimal Amount, string Currency, string ReferenceCode);
