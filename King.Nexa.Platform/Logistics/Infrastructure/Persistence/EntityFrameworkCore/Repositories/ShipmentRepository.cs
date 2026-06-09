using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Logistics.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Logistics.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class ShipmentRepository(AppDbContext context) : BaseRepository<Shipment>(context), IShipmentRepository
{
    public async Task<Shipment?> FindByShipmentCodeAsync(ShipmentCode shipmentCode, CancellationToken cancellationToken = default) =>
        await Context.Shipments.FirstOrDefaultAsync(shipment => shipment.ShipmentCode == shipmentCode, cancellationToken);

    public async Task<IEnumerable<Shipment>> ListByOrderIdAsync(int orderId, CancellationToken cancellationToken = default) =>
        await Context.Shipments.Where(shipment => shipment.OrderId == orderId).ToListAsync(cancellationToken);

    public async Task<IEnumerable<Shipment>> ListByStatusAsync(DeliveryStatus status, CancellationToken cancellationToken = default) =>
        await Context.Shipments.Where(shipment => shipment.Status == status).ToListAsync(cancellationToken);
}
