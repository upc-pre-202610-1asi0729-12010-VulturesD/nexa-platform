using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using Microsoft.EntityFrameworkCore;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class WarehouseRepository(AppDbContext context) : BaseRepository<WarehouseAggregate>(context), IWarehouseRepository
{
    public async Task<WarehouseAggregate?> FindByLocationAsync(WarehouseLocation location, CancellationToken cancellationToken = default) =>
        await Context.Warehouses.FirstOrDefaultAsync(warehouse => warehouse.Location == location, cancellationToken);
}
