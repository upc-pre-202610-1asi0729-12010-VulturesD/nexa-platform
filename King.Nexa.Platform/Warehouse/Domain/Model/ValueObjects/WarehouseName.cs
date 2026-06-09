namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record WarehouseName
{
    public WarehouseName(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Warehouse name is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
