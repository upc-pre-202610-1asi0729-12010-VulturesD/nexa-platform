using King.Nexa.Platform.Warehouse.Application.QueryServices;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Application.Internal.QueryServices;

public class WarehouseQueryService(IWarehouseRepository warehouseRepository) : IWarehouseQueryService
{
    public async Task<IEnumerable<WarehouseAggregate>> Handle(GetAllWarehousesQuery query, CancellationToken cancellationToken = default) =>
        await warehouseRepository.ListAsync(cancellationToken);

    public async Task<WarehouseAggregate?> Handle(GetWarehouseByIdQuery query, CancellationToken cancellationToken = default) =>
        await warehouseRepository.FindByIdAsync(query.WarehouseId, cancellationToken);

    public async Task<WarehouseAggregate?> Handle(GetWarehouseByLocationQuery query, CancellationToken cancellationToken = default) =>
        await warehouseRepository.FindByLocationAsync(new WarehouseLocation(query.Location), cancellationToken);
}
