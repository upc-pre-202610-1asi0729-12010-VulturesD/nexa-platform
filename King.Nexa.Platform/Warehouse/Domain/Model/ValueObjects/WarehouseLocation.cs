namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record WarehouseLocation
{
    public WarehouseLocation(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Warehouse location is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
