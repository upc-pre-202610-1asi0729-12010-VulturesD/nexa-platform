using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Application.QueryServices;

public interface IWarehouseQueryService
{
    Task<IEnumerable<WarehouseAggregate>> Handle(GetAllWarehousesQuery query, CancellationToken cancellationToken = default);

    Task<WarehouseAggregate?> Handle(GetWarehouseByIdQuery query, CancellationToken cancellationToken = default);

    Task<WarehouseAggregate?> Handle(GetWarehouseByLocationQuery query, CancellationToken cancellationToken = default);
}
