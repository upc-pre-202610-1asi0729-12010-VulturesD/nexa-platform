namespace King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

public sealed record BillingAmount(decimal Amount, string Currency)
{
    public BillingAmount(decimal amount) : this(amount, "PEN")
    {
    }
}
