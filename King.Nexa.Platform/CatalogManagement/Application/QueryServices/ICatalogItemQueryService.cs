using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;

namespace King.Nexa.Platform.CatalogManagement.Application.QueryServices;

/// <summary>
/// Defines catalog item read operations exposed by the application layer.
/// </summary>
public interface ICatalogItemQueryService
{
    /// <summary>
    /// Retrieves all catalog items available in the platform catalog.
    /// </summary>
    Task<IEnumerable<CatalogItem>> Handle(GetAllCatalogItemsQuery query, CancellationToken cancellationToken = default);

    /// <summary>
    /// Retrieves a catalog item by its internal numeric identifier.
    /// </summary>
    Task<CatalogItem?> Handle(GetCatalogItemByIdQuery query, CancellationToken cancellationToken = default);
}
