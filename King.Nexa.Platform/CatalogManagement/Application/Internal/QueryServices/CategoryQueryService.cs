using King.Nexa.Platform.CatalogManagement.Application.QueryServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Queries;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.QueryServices;

public class CategoryQueryService(ICategoryRepository categoryRepository) : ICategoryQueryService
{
    public async Task<IEnumerable<Category>> Handle(GetAllCategoriesQuery query, CancellationToken cancellationToken = default) =>
        await categoryRepository.ListAsync(cancellationToken);

    public async Task<Category?> Handle(GetCategoryByIdQuery query, CancellationToken cancellationToken = default) =>
        await categoryRepository.FindByIdAsync(query.CategoryId, cancellationToken);

    public async Task<Category?> Handle(GetCategoryByNameQuery query, CancellationToken cancellationToken = default) =>
        await categoryRepository.FindByNameAsync(new CategoryName(query.Name), cancellationToken);
}
