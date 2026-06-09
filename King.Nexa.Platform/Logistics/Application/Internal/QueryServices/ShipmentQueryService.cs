using King.Nexa.Platform.Logistics.Application.CommandServices;
using King.Nexa.Platform.Logistics.Application.QueryServices;
using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.Queries;
using King.Nexa.Platform.Logistics.Domain.Repositories;

namespace King.Nexa.Platform.Logistics.Application.Internal.QueryServices;

public class ShipmentQueryService(IShipmentRepository shipmentRepository) : IShipmentQueryService
{
    public async Task<IEnumerable<Shipment>> Handle(GetAllShipmentsQuery query, CancellationToken cancellationToken = default) =>
        await shipmentRepository.ListAsync(cancellationToken);

    public async Task<Shipment?> Handle(GetShipmentByIdQuery query, CancellationToken cancellationToken = default) =>
        await shipmentRepository.FindByIdAsync(query.ShipmentId, cancellationToken);

    public async Task<IEnumerable<Shipment>> Handle(GetShipmentsByOrderIdQuery query, CancellationToken cancellationToken = default) =>
        await shipmentRepository.ListByOrderIdAsync(query.OrderId, cancellationToken);

    public async Task<IEnumerable<Shipment>> Handle(GetShipmentsByStatusQuery query, CancellationToken cancellationToken = default) =>
        await shipmentRepository.ListByStatusAsync(query.Status, cancellationToken);
}
