namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record LotCode
{
    public LotCode(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Lot code is required.", nameof(value));
        Value = value.Trim().ToUpperInvariant();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
