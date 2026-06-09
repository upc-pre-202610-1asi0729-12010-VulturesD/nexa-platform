using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record CreateWarehouseCommand(WarehouseName Name, WarehouseLocation Location, TemperatureRange TemperatureRange);
