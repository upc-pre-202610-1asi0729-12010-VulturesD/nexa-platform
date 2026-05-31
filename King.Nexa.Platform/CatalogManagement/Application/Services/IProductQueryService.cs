using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;

namespace King.Nexa.Platform.CatalogManagement.Application.Services;

public interface IProductQueryService
{
    Task<IEnumerable<Product>> Handle(GetAllProductsQuery query, CancellationToken cancellationToken = default);

    Task<Product?> Handle(GetProductByIdQuery query, CancellationToken cancellationToken = default);
}
