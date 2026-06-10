using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.Queries;

namespace King.Nexa.Platform.Logistics.Application.QueryServices;

public interface IShipmentQueryService
{
    Task<IEnumerable<Shipment>> Handle(GetAllShipmentsQuery query, CancellationToken cancellationToken = default);

    Task<Shipment?> Handle(GetShipmentByIdQuery query, CancellationToken cancellationToken = default);

    Task<IEnumerable<Shipment>> Handle(GetShipmentsByOrderIdQuery query, CancellationToken cancellationToken = default);

    Task<IEnumerable<Shipment>> Handle(GetShipmentsByStatusQuery query, CancellationToken cancellationToken = default);
}
