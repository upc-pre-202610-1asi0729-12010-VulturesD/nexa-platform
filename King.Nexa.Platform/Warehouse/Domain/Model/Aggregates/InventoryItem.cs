using King.Nexa.Platform.Shared.Domain.Model.Entities;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;

/// <summary>
/// Aggregate root for stock availability and reservation state.
/// </summary>
public class InventoryItem : AuditableEntity
{
    protected InventoryItem()
    {
        ProductId = null!;
        CatalogItemId = null!;
        AvailableQuantity = null!;
        ReservedQuantity = null!;
        WarehouseLocation = null!;
        TemperatureRange = null!;
    }

    public InventoryItem(CreateInventoryItemCommand command)
    {
        ProductId = command.ProductId;
        CatalogItemId = command.CatalogItemId;
        AvailableQuantity = command.AvailableQuantity;
        ReservedQuantity = new StockQuantity(0);
        WarehouseLocation = command.WarehouseLocation;
        TemperatureRange = command.TemperatureRange;
    }

    public ProductId ProductId { get; private set; }

    public CatalogItemId CatalogItemId { get; private set; }

    public StockQuantity AvailableQuantity { get; private set; }

    public StockQuantity ReservedQuantity { get; private set; }

    public WarehouseLocation WarehouseLocation { get; private set; }

    public TemperatureRange TemperatureRange { get; private set; }

    public void Reserve(InventoryReservation reservation)
    {
        if (reservation.Units > AvailableQuantity.Value)
            throw new InvalidOperationException("Requested units exceed available stock.");

        AvailableQuantity = new StockQuantity(AvailableQuantity.Value - reservation.Units);
        ReservedQuantity = new StockQuantity(ReservedQuantity.Value + reservation.Units);
    }

    public void Release(InventoryReservation reservation)
    {
        if (reservation.Units > ReservedQuantity.Value)
            throw new InvalidOperationException("Released units exceed reserved stock.");

        AvailableQuantity = new StockQuantity(AvailableQuantity.Value + reservation.Units);
        ReservedQuantity = new StockQuantity(ReservedQuantity.Value - reservation.Units);
    }
}
