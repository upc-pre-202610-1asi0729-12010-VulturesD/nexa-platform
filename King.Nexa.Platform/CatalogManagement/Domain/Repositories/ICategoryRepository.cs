using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Domain.Repositories;

public interface ICategoryRepository : IBaseRepository<Category>
{
    Task<Category?> FindByNameAsync(CategoryName name, CancellationToken cancellationToken = default);
}
