using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;

namespace King.Nexa.Platform.Warehouse.Application.QueryServices;

public interface IInventoryItemQueryService
{
    Task<IEnumerable<InventoryItem>> Handle(GetAllInventoryItemsQuery query, CancellationToken cancellationToken = default);

    Task<InventoryItem?> Handle(GetInventoryItemByIdQuery query, CancellationToken cancellationToken = default);

    Task<InventoryItem?> Handle(GetInventoryItemByCatalogItemIdQuery query, CancellationToken cancellationToken = default);
}
