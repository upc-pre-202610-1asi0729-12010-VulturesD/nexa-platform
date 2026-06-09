using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Transform;

public static class UpdateInvoiceCommandFromResourceAssembler
{
    public static UpdateInvoiceCommand ToCommandFromResource(int id, UpdateInvoiceResource resource) =>
        new(id, new InvoiceNumber(resource.InvoiceNumber), resource.OrderId, new BillingAmount(resource.Amount, resource.Currency));
}
