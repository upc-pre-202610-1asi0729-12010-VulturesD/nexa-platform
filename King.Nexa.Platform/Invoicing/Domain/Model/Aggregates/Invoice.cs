using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

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

    public void Update(UpdateInvoiceCommand command)
    {
        if (PaymentStatus == PaymentStatus.Paid) throw new InvalidOperationException("Paid invoices cannot be updated.");
        if (PaymentStatus == PaymentStatus.Cancelled) throw new InvalidOperationException("Cancelled invoices cannot be updated.");

        InvoiceNumber = command.InvoiceNumber;
        OrderId = command.OrderId;
        BillingAmount = command.BillingAmount;
    }

    public void Cancel()
    {
        if (PaymentStatus == PaymentStatus.Paid) throw new InvalidOperationException("Paid invoices cannot be cancelled.");

        PaymentStatus = PaymentStatus.Cancelled;
    }

    public void MarkPaid()
    {
        if (PaymentStatus == PaymentStatus.Cancelled) throw new InvalidOperationException("Cancelled invoices cannot be paid.");

        PaymentStatus = PaymentStatus.Paid;
        PaidAt = DateTimeOffset.UtcNow;
    }
}
