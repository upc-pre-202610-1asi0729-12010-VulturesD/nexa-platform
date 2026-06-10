using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Logistics.Domain.Repositories;

public interface IShipmentRepository : IBaseRepository<Shipment>
{
    Task<Shipment?> FindByShipmentCodeAsync(ShipmentCode shipmentCode, CancellationToken cancellationToken = default);

    Task<IEnumerable<Shipment>> ListByOrderIdAsync(int orderId, CancellationToken cancellationToken = default);

    Task<IEnumerable<Shipment>> ListByStatusAsync(DeliveryStatus status, CancellationToken cancellationToken = default);
}
