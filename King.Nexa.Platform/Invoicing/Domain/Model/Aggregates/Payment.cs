using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;

public class Payment : AuditableEntity
{
    protected Payment()
    {
        BillingAmount = null!;
        ReferenceCode = string.Empty;
    }

    public Payment(RegisterPaymentCommand command)
    {
        InvoiceId = command.InvoiceId;
        BillingAmount = command.BillingAmount;
        ReferenceCode = command.ReferenceCode.Trim().ToUpperInvariant();
        Status = PaymentStatus.Pending;
    }

    public int InvoiceId { get; private set; }

    public BillingAmount BillingAmount { get; private set; }

    public string ReferenceCode { get; private set; }

    public PaymentStatus Status { get; private set; }

    public void Update(UpdatePaymentCommand command)
    {
        if (Status != PaymentStatus.Pending)
            throw new InvalidOperationException("Only pending payments can be updated.");

        InvoiceId = command.InvoiceId;
        BillingAmount = command.BillingAmount;
        ReferenceCode = command.ReferenceCode.Trim().ToUpperInvariant();
    }

    public void Cancel()
    {
        if (Status == PaymentStatus.Paid) throw new InvalidOperationException("Paid payments cannot be cancelled.");

        Status = PaymentStatus.Cancelled;
    }
}
