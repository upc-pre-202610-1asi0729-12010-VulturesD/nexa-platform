namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

public record PaymentResource(int Id, int InvoiceId, decimal Amount, string Currency, string ReferenceCode, string Status);
