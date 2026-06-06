namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

public sealed record PaymentConfirmation
{
    public PaymentConfirmation(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Payment confirmation is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
