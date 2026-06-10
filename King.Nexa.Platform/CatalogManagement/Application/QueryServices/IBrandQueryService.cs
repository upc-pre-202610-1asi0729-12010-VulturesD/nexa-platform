using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;

namespace King.Nexa.Platform.CatalogManagement.Application.QueryServices;

public interface IBrandQueryService
{
    Task<IEnumerable<Brand>> Handle(GetAllBrandsQuery query, CancellationToken cancellationToken = default);

    Task<Brand?> Handle(GetBrandByIdQuery query, CancellationToken cancellationToken = default);

    Task<Brand?> Handle(GetBrandByNameQuery query, CancellationToken cancellationToken = default);
}
