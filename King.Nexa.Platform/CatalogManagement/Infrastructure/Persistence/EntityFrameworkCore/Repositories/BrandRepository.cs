using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class BrandRepository(AppDbContext context) : BaseRepository<Brand>(context), IBrandRepository
{
    public async Task<Brand?> FindByNameAsync(BrandName name, CancellationToken cancellationToken = default) =>
        await Context.Brands.FirstOrDefaultAsync(brand => brand.Name == name, cancellationToken);
}
