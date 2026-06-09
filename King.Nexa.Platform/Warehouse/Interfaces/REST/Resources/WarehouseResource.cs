namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

public record WarehouseResource(
    int Id,
    string Name,
    string Location,
    decimal MinimumTemperature,
    decimal MaximumTemperature,
    bool IsActive);
