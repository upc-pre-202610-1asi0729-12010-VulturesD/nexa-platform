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
}
