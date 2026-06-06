namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

public record GenerateInvoiceResource(string InvoiceNumber, int OrderId, decimal Amount, string Currency);
