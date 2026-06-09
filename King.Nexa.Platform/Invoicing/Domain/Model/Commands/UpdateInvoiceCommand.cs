using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Commands;

public record UpdateInvoiceCommand(int InvoiceId, InvoiceNumber InvoiceNumber, int OrderId, BillingAmount BillingAmount);
