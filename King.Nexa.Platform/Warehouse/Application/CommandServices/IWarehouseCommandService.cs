using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Application.CommandServices;

public interface IWarehouseCommandService
{
    Task<WarehouseAggregate> CreateAsync(CreateWarehouseCommand command, CancellationToken cancellationToken = default);

    Task<WarehouseAggregate?> UpdateAsync(UpdateWarehouseCommand command, CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(DeleteWarehouseCommand command, CancellationToken cancellationToken = default);
}
