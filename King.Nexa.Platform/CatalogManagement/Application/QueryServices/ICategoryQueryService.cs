using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;

namespace King.Nexa.Platform.CatalogManagement.Application.QueryServices;

public interface ICategoryQueryService
{
    Task<IEnumerable<Category>> Handle(GetAllCategoriesQuery query, CancellationToken cancellationToken = default);

    Task<Category?> Handle(GetCategoryByIdQuery query, CancellationToken cancellationToken = default);

    Task<Category?> Handle(GetCategoryByNameQuery query, CancellationToken cancellationToken = default);
}
