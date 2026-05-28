namespace King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

public sealed record StorageLocation(string Zone, string Rack)
{
    public string Label => $"{Zone}-{Rack}";
}
