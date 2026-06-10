using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;

/// <summary>
/// Handles catalog item read use cases backed by the catalog repository.
/// </summary>
public class CatalogItemQueryService(ICatalogItemRepository catalogItemRepository) : ICatalogItemQueryService
{
    /// <summary>
    /// Handles the query that lists all catalog items.
    /// </summary>
    public async Task<IEnumerable<CatalogItem>> Handle(GetAllCatalogItemsQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.ListAsync(cancellationToken);

    /// <summary>
    /// Handles the query that returns one catalog item by id.
    /// </summary>
    public async Task<CatalogItem?> Handle(GetCatalogItemByIdQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.FindByIdAsync(query.Id, cancellationToken);

    public async Task<CatalogItem?> Handle(GetCatalogItemByCatalogItemIdQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.FindByCatalogItemIdAsync(new CatalogItemId(query.CatalogItemId), cancellationToken);

    public async Task<IEnumerable<CatalogItem>> Handle(GetCatalogItemsByCategoryNameQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.ListByCategoryNameAsync(new CategoryName(query.CategoryName), cancellationToken);

    public async Task<IEnumerable<CatalogItem>> Handle(GetCatalogItemsByBrandNameQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.ListByBrandNameAsync(new BrandName(query.BrandName), cancellationToken);

    public async Task<IEnumerable<CatalogItem>> Handle(GetCatalogItemsByColdChainRequirementQuery query, CancellationToken cancellationToken = default) =>
        await catalogItemRepository.ListByColdChainRequirementAsync(query.ColdChainRequirement, cancellationToken);
}
