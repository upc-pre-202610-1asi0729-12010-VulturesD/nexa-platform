using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EFC.Repositories;

public class InventoryItemRepository(AppDbContext context) : BaseRepository<InventoryItem>(context), IInventoryItemRepository
{
    public async Task<InventoryItem?> FindByProductAndLotAsync(string productCode, LotCode lotCode, CancellationToken cancellationToken = default) =>
        await Context.InventoryItems.FirstOrDefaultAsync(item => item.ProductCode == productCode && item.LotCode == lotCode, cancellationToken);
}
