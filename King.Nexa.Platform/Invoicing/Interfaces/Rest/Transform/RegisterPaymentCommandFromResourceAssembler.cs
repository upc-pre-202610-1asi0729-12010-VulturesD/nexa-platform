using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Interfaces.Rest.Resources;

namespace King.Nexa.Platform.Invoicing.Interfaces.Rest.Transform;

public static class RegisterPaymentCommandFromResourceAssembler
{
    public static RegisterPaymentCommand ToCommandFromResource(RegisterPaymentResource resource) =>
        new(resource.InvoiceId, new BillingAmount(resource.Amount, resource.Currency), resource.ReferenceCode);
}
