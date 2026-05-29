using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Commands;

public record GenerateInvoiceCommand(InvoiceNumber InvoiceNumber, int OrderId, BillingAmount BillingAmount);
