using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;

public class CatalogItemQueryService(ICatalogItemRepository catalogItemRepository) : ICatalogItemQueryService
{
    public async Task<IEnumerable<CatalogItem>> Handle(GetAllCatalogItemsQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.ListAsync(cancellationToken);

    public async Task<CatalogItem?> Handle(GetCatalogItemByIdQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.FindByIdAsync(query.Id, cancellationToken);
}
