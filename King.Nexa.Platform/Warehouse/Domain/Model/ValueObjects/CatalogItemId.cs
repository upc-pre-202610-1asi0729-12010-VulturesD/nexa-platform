namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record CatalogItemId
{
    public CatalogItemId(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Catalog item id is required.", nameof(value));
        Value = value.Trim().ToUpperInvariant();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
