namespace King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

public sealed record InvoiceNumber
{
    public InvoiceNumber(string value)
    {
        if (string.IsNullOrWhiteSpace(value)) throw new ArgumentException("Invoice number is required.", nameof(value));
        Value = value.Trim().ToUpperInvariant();
    }

    public string Value { get; }

    public override string ToString() => Value;
}
