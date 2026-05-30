using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;

namespace King.Nexa.Platform.Warehouse.Application.Services;

public interface IInventoryItemCommandService
{
    Task<InventoryItem> SyncAsync(SyncInventoryCommand command, CancellationToken cancellationToken = default);

    Task<InventoryItem?> ReserveAsync(ReserveInventoryCommand command, CancellationToken cancellationToken = default);
}
