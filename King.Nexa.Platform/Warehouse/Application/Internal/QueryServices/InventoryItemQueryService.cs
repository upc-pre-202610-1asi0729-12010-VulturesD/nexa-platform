using King.Nexa.Platform.Warehouse.Application.Services;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Domain.Repositories;

namespace King.Nexa.Platform.Warehouse.Application.Internal.QueryServices;

public class InventoryItemQueryService(IInventoryItemRepository inventoryItemRepository) : IInventoryItemQueryService
{
    public async Task<IEnumerable<InventoryItem>> Handle(GetAllInventoryItemsQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.ListAsync(cancellationToken);

    public async Task<InventoryItem?> Handle(GetInventoryItemByIdQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.FindByIdAsync(query.InventoryItemId, cancellationToken);
}
