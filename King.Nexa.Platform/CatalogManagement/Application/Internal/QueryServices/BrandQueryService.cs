using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;

public class BrandQueryService(IBrandRepository brandRepository) : IBrandQueryService
{
    public async Task<IEnumerable<Brand>> Handle(GetAllBrandsQuery query, CancellationToken cancellationToken = default) =>
        await brandRepository.ListAsync(cancellationToken);

    public async Task<Brand?> Handle(GetBrandByIdQuery query, CancellationToken cancellationToken = default) =>
        await brandRepository.FindByIdAsync(query.BrandId, cancellationToken);

    public async Task<Brand?> Handle(GetBrandByNameQuery query, CancellationToken cancellationToken = default) =>
        await brandRepository.FindByNameAsync(new BrandName(query.Name), cancellationToken);
}
