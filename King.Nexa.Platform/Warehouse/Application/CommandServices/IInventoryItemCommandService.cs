using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;

namespace King.Nexa.Platform.Warehouse.Application.CommandServices;

public interface IInventoryItemCommandService
{
    Task<InventoryItem> CreateAsync(CreateInventoryItemCommand command, CancellationToken cancellationToken = default);

    Task<InventoryItem?> UpdateAsync(UpdateInventoryItemCommand command, CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(DeleteInventoryItemCommand command, CancellationToken cancellationToken = default);

    Task<InventoryItem?> ReserveAsync(ReserveInventoryCommand command, CancellationToken cancellationToken = default);

    Task<InventoryItem?> ReleaseAsync(ReleaseInventoryReservationCommand command, CancellationToken cancellationToken = default);
}
