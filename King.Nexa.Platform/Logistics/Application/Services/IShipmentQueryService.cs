using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.Queries;

namespace King.Nexa.Platform.Logistics.Application.Services;

public interface IShipmentQueryService
{
    Task<IEnumerable<Shipment>> Handle(GetAllShipmentsQuery query, CancellationToken cancellationToken = default);

    Task<Shipment?> Handle(GetShipmentByIdQuery query, CancellationToken cancellationToken = default);
}
