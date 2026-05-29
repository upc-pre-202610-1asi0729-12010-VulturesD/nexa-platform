using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;

public class Invoice : AuditableEntity
{
    protected Invoice()
    {
        InvoiceNumber = null!;
        BillingAmount = null!;
    }

    public Invoice(GenerateInvoiceCommand command)
    {
        InvoiceNumber = command.InvoiceNumber;
        OrderId = command.OrderId;
        BillingAmount = command.BillingAmount;
        PaymentStatus = PaymentStatus.Pending;
    }

    public InvoiceNumber InvoiceNumber { get; private set; }

    public int OrderId { get; private set; }

    public BillingAmount BillingAmount { get; private set; }

    public PaymentStatus PaymentStatus { get; private set; }

    public DateTimeOffset? PaidAt { get; private set; }

    public void MarkPaid()
    {
        PaymentStatus = PaymentStatus.Paid;
        PaidAt = DateTimeOffset.UtcNow;
    }
}
