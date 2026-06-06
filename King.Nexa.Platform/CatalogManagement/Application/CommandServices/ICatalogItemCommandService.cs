using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

namespace King.Nexa.Platform.CatalogManagement.Application.CommandServices;

public interface ICatalogItemCommandService
{
    Task<CatalogItem> CreateAsync(CreateCatalogItemCommand command, CancellationToken cancellationToken = default);
}
