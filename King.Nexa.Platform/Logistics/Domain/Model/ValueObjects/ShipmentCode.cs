namespace King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;

public sealed record ShipmentCode
{
    public ShipmentCode(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Shipment code is required.", nameof(value));
        Value = value.Trim().ToUpperInvariant();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
