namespace King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

public sealed record CategoryName
{
    public CategoryName(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Category name is required.", nameof(value));
        Value = value.Trim();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
