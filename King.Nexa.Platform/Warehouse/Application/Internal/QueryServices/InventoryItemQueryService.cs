using King.Nexa.Platform.Warehouse.Application.CommandServices;
using King.Nexa.Platform.Warehouse.Application.QueryServices;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;
using King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects;
using King.Nexa.Platform.Warehouse.Domain.Repositories;

namespace King.Nexa.Platform.Warehouse.Application.Internal.QueryServices;

public class InventoryItemQueryService(IInventoryItemRepository inventoryItemRepository) : IInventoryItemQueryService
{
    public async Task<IEnumerable<InventoryItem>> Handle(GetAllInventoryItemsQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.ListAsync(cancellationToken);

    public async Task<InventoryItem?> Handle(GetInventoryItemByIdQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.FindByIdAsync(query.InventoryItemId, cancellationToken);

    public async Task<InventoryItem?> Handle(GetInventoryItemByCatalogItemIdQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.FindByCatalogItemIdAsync(new CatalogItemId(query.CatalogItemId), cancellationToken);

    public async Task<IEnumerable<InventoryItem>> Handle(GetInventoryItemsByWarehouseLocationQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.ListByWarehouseLocationAsync(new WarehouseLocation(query.WarehouseLocation), cancellationToken);

    public async Task<IEnumerable<InventoryItem>> Handle(GetLowStockInventoryItemsQuery query, CancellationToken cancellationToken = default) =>
        await inventoryItemRepository.ListLowStockAsync(query.Threshold, cancellationToken);
}
