using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Application.CommandServices;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using WarehouseAggregate = King.Nexa.Platform.Warehouse.Domain.Model.Aggregates.Warehouse;

namespace King.Nexa.Platform.Warehouse.Application.Internal.CommandServices;

public class WarehouseCommandService(IWarehouseRepository warehouseRepository, IUnitOfWork unitOfWork) : IWarehouseCommandService
{
    public async Task<WarehouseAggregate> CreateAsync(CreateWarehouseCommand command, CancellationToken cancellationToken = default)
    {
        var warehouse = new WarehouseAggregate(command);
        await warehouseRepository.AddAsync(warehouse, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return warehouse;
    }

    public async Task<WarehouseAggregate?> UpdateAsync(UpdateWarehouseCommand command, CancellationToken cancellationToken = default)
    {
        var warehouse = await warehouseRepository.FindByIdAsync(command.WarehouseId, cancellationToken);
        if (warehouse is null) return null;

        warehouse.Update(command);
        warehouseRepository.Update(warehouse);
        await unitOfWork.CompleteAsync(cancellationToken);
        return warehouse;
    }

    public async Task<bool> DeleteAsync(DeleteWarehouseCommand command, CancellationToken cancellationToken = default)
    {
        var warehouse = await warehouseRepository.FindByIdAsync(command.WarehouseId, cancellationToken);
        if (warehouse is null) return false;

        warehouse.Deactivate();
        warehouseRepository.Update(warehouse);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
