using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;

/// <summary>
/// Coordinates catalog item creation and persistence.
/// </summary>
public class CatalogItemCommandService(ICatalogItemRepository catalogItemRepository, IUnitOfWork unitOfWork) : ICatalogItemCommandService
{
    /// <summary>
    /// Creates a catalog item and commits the unit of work.
    /// </summary>
    public async Task<CatalogItem> CreateAsync(CreateCatalogItemCommand command, CancellationToken cancellationToken = default)
    {
        var catalogItem = new CatalogItem(command);
        await catalogItemRepository.AddAsync(catalogItem, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return catalogItem;
    }
}
