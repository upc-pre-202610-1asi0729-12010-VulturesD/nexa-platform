namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

public sealed record OrderNumber
{
    public OrderNumber(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Order number is required.", nameof(value));
        Value = value.Trim().ToUpperInvariant();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
