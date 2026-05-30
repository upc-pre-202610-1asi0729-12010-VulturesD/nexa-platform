using King.Nexa.Platform.CatalogManagement.Application.Services;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;

public class ProductQueryService(IProductRepository productRepository) : IProductQueryService
{
    public async Task<IEnumerable<Product>> Handle(GetAllProductsQuery query, CancellationToken cancellationToken = default) =>
        await productRepository.ListAsync(cancellationToken);

    public async Task<Product?> Handle(GetProductByIdQuery query, CancellationToken cancellationToken = default) =>
        await productRepository.FindByIdAsync(query.ProductId, cancellationToken);
}
