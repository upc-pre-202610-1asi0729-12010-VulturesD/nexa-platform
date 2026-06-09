using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

namespace King.Nexa.Platform.CatalogManagement.Application.CommandServices;

/// <summary>
/// Defines catalog item write operations exposed by the application layer.
/// </summary>
public interface ICatalogItemCommandService
{
    /// <summary>
    /// Creates a new catalog item from the supplied command.
    /// </summary>
    Task<CatalogItem> CreateAsync(CreateCatalogItemCommand command, CancellationToken cancellationToken = default);

    Task<CatalogItem?> UpdateAsync(UpdateCatalogItemCommand command, CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(DeleteCatalogItemCommand command, CancellationToken cancellationToken = default);
}
