using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Domain.Repositories;

public interface IWarehouseRepository : IBaseRepository<WarehouseAggregate>
{
    Task<WarehouseAggregate?> FindByLocationAsync(WarehouseLocation location, CancellationToken cancellationToken = default);
}
