using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class CategoryRepository(AppDbContext context) : BaseRepository<Category>(context), ICategoryRepository
{
    public async Task<Category?> FindByNameAsync(CategoryName name, CancellationToken cancellationToken = default) =>
        await Context.Categories.FirstOrDefaultAsync(category => category.Name == name, cancellationToken);
}
