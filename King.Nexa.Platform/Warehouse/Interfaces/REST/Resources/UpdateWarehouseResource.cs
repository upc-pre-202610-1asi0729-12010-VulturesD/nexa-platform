namespace King.Nexa.Platform.Warehouse.Interfaces.Rest.Resources;

public record UpdateWarehouseResource(string Name, string Location, decimal MinimumTemperature, decimal MaximumTemperature);
