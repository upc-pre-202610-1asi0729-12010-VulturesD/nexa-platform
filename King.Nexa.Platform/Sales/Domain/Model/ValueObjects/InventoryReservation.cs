namespace King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

public sealed record InventoryReservation
{
    public InventoryReservation(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Inventory reservation is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
