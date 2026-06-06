namespace King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

public sealed record ItemName
{
    public ItemName(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Item name is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
