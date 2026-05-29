using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Interfaces.REST.Resources;

namespace King.Nexa.Platform.Invoicing.Interfaces.REST.Transform;

public static class InvoiceResourceFromEntityAssembler
{
    public static InvoiceResource ToResourceFromEntity(Invoice entity) =>
        new(entity.Id, entity.InvoiceNumber.Value, entity.OrderId, entity.BillingAmount.Amount, entity.BillingAmount.Currency, entity.PaymentStatus.ToString(), entity.PaidAt);
}
