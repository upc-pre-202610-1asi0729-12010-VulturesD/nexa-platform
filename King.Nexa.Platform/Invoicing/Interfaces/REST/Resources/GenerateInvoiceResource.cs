namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

/// <summary>
/// Data required to generate an invoice for an order.
/// </summary>
public record GenerateInvoiceResource(string InvoiceNumber, int OrderId, decimal Amount, string Currency);
