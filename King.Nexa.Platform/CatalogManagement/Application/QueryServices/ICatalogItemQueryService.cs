using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;

namespace King.Nexa.Platform.CatalogManagement.Application.QueryServices;

public interface ICatalogItemQueryService
{
    Task<IEnumerable<CatalogItem>> Handle(GetAllCatalogItemsQuery query, CancellationToken cancellationToken = default);

    Task<CatalogItem?> Handle(GetCatalogItemByIdQuery query, CancellationToken cancellationToken = default);
}
