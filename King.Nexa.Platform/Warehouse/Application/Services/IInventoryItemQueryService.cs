using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Queries;

namespace King.Nexa.Platform.Warehouse.Application.Services;

public interface IInventoryItemQueryService
{
    Task<IEnumerable<InventoryItem>> Handle(GetAllInventoryItemsQuery query, CancellationToken cancellationToken = default);

    Task<InventoryItem?> Handle(GetInventoryItemByIdQuery query, CancellationToken cancellationToken = default);
}
