namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

/// <summary>
/// Data required to update an unpaid invoice.
/// </summary>
public record UpdateInvoiceResource(string InvoiceNumber, int OrderId, decimal Amount, string Currency);
