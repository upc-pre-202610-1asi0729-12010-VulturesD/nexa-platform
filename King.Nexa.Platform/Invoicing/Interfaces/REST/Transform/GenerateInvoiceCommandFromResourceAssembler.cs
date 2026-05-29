using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Invoicing.Interfaces.REST.Transform;

public static class GenerateInvoiceCommandFromResourceAssembler
{
    public static GenerateInvoiceCommand ToCommandFromResource(GenerateInvoiceResource resource) =>
        new(new InvoiceNumber(resource.InvoiceNumber), resource.OrderId, new BillingAmount(resource.Amount, resource.Currency));
}
