namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

/// <summary>
/// Invoice response resource with amount and payment status.
/// </summary>
public record InvoiceResource(int Id, string InvoiceNumber, int OrderId, decimal Amount, string Currency, string PaymentStatus, DateTimeOffset? PaidAt);
