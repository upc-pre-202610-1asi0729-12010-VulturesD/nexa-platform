namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

public sealed record CustomerId
{
    public CustomerId(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Customer id is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
