namespace King.Nexa.Platform.Invoicing.Interfaces.REST.Resources;

public record GenerateInvoiceResource(string InvoiceNumber, int OrderId, decimal Amount, string Currency);
