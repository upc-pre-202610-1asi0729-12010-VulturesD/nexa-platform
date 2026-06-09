using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Commands;

public record UpdateWarehouseCommand(int WarehouseId, WarehouseName Name, WarehouseLocation Location, TemperatureRange TemperatureRange);
