using King.Nexa.Platform.CatalogManagement.Application.Services;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;

public class ProductCommandService(IProductRepository productRepository, IUnitOfWork unitOfWork) : IProductCommandService
{
    public async Task<Product> CreateAsync(CreateProductCommand command, CancellationToken cancellationToken = default)
    {
        var product = new Product(command);
        await productRepository.AddAsync(product, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return product;
    }
}
