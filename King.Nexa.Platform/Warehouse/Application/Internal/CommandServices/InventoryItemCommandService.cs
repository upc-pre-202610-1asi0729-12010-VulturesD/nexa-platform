using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Application.Services;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Repositories;

namespace King.Nexa.Platform.Warehouse.Application.Internal.CommandServices;

public class InventoryItemCommandService(IInventoryItemRepository inventoryItemRepository, IUnitOfWork unitOfWork) : IInventoryItemCommandService
{
    public async Task<InventoryItem> SyncAsync(SyncInventoryCommand command, CancellationToken cancellationToken = default)
    {
        var item = new InventoryItem(command);
        await inventoryItemRepository.AddAsync(item, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return item;
    }

    public async Task<InventoryItem?> ReserveAsync(ReserveInventoryCommand command, CancellationToken cancellationToken = default)
    {
        var item = await inventoryItemRepository.FindByIdAsync(command.InventoryItemId, cancellationToken);
        if (item is null) return null;

        item.Reserve(command.Units);
        inventoryItemRepository.Update(item);
        await unitOfWork.CompleteAsync(cancellationToken);
        return item;
    }
}
