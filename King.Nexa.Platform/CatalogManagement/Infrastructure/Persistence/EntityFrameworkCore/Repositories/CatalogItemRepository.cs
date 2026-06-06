using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class CatalogItemRepository(AppDbContext context) : BaseRepository<CatalogItem>(context), ICatalogItemRepository
{
    public async Task<CatalogItem?> FindByCatalogItemIdAsync(CatalogItemId catalogItemId, CancellationToken cancellationToken = default) =>
        await Context.CatalogItems.FirstOrDefaultAsync(catalogItem => catalogItem.CatalogItemId == catalogItemId, cancellationToken);
}
