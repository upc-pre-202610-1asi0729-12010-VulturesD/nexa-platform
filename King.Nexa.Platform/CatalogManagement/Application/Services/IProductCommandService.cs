using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

namespace King.Nexa.Platform.CatalogManagement.Application.Services;

public interface IProductCommandService
{
    Task<Product> CreateAsync(CreateProductCommand command, CancellationToken cancellationToken = default);
}
