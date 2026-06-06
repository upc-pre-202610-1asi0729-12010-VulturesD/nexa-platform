namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

public record InvoiceResource(int Id, string InvoiceNumber, int OrderId, decimal Amount, string Currency, string PaymentStatus, DateTimeOffset? PaidAt);
