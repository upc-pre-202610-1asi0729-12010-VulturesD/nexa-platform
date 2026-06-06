using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Domain.Repositories;

public interface ICatalogItemRepository : IBaseRepository<CatalogItem>
{
    Task<CatalogItem?> FindByCatalogItemIdAsync(CatalogItemId catalogItemId, CancellationToken cancellationToken = default);
}
