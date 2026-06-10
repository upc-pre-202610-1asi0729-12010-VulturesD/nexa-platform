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

    public async Task<CatalogItem?> UpdateAsync(UpdateCatalogItemCommand command, CancellationToken cancellationToken = default)
    {
        var catalogItem = await catalogItemRepository.FindByIdAsync(command.Id, cancellationToken);
        if (catalogItem is null) return null;

        catalogItem.Update(command);
        catalogItemRepository.Update(catalogItem);
        await unitOfWork.CompleteAsync(cancellationToken);
        return catalogItem;
    }

    public async Task<bool> DeleteAsync(DeleteCatalogItemCommand command, CancellationToken cancellationToken = default)
    {
        var catalogItem = await catalogItemRepository.FindByIdAsync(command.CatalogItemId, cancellationToken);
        if (catalogItem is null) return false;

        catalogItem.Deactivate();
        catalogItemRepository.Update(catalogItem);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
