using King.Nexa.Platform.Shared.Domain.Model;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;

public class InventoryItem : AuditableEntity
{
    protected InventoryItem()
    {
        ProductCode = string.Empty;
        LotCode = null!;
        StorageLocation = null!;
        StockQuantity = null!;
    }

    public InventoryItem(SyncInventoryCommand command)
    {
        ProductCode = command.ProductCode.Trim().ToUpperInvariant();
        LotCode = command.LotCode;
        StorageLocation = command.StorageLocation;
        StockQuantity = command.StockQuantity;
    }

    public string ProductCode { get; private set; }

    public LotCode LotCode { get; private set; }

    public StorageLocation StorageLocation { get; private set; }

    public StockQuantity StockQuantity { get; private set; }

    public void Reserve(int units) => StockQuantity = StockQuantity.Reserve(units);
}
