namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

public record CreateWarehouseResource(string Name, string Location, decimal MinimumTemperature, decimal MaximumTemperature);
