using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class InventoryItemRepository(AppDbContext context) : BaseRepository<InventoryItem>(context), IInventoryItemRepository
{
    public async Task<InventoryItem?> FindByCatalogItemIdAsync(CatalogItemId catalogItemId, CancellationToken cancellationToken = default) =>
        await Context.InventoryItems.FirstOrDefaultAsync(item => item.CatalogItemId == catalogItemId, cancellationToken);

    public async Task<IEnumerable<InventoryItem>> ListByWarehouseLocationAsync(WarehouseLocation warehouseLocation, CancellationToken cancellationToken = default) =>
        await Context.InventoryItems.Where(item => item.WarehouseLocation == warehouseLocation).ToListAsync(cancellationToken);

    public async Task<IEnumerable<InventoryItem>> ListLowStockAsync(int threshold, CancellationToken cancellationToken = default)
    {
        var items = await Context.InventoryItems.ToListAsync(cancellationToken);
        return items.Where(item => item.AvailableQuantity.Value <= threshold);
    }
}
