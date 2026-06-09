using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Warehouse.Domain.Repositories;

public interface IInventoryItemRepository : IBaseRepository<InventoryItem>
{
    Task<InventoryItem?> FindByCatalogItemIdAsync(CatalogItemId catalogItemId, CancellationToken cancellationToken = default);

    Task<IEnumerable<InventoryItem>> ListByWarehouseLocationAsync(WarehouseLocation warehouseLocation, CancellationToken cancellationToken = default);

    Task<IEnumerable<InventoryItem>> ListLowStockAsync(int threshold, CancellationToken cancellationToken = default);
}
