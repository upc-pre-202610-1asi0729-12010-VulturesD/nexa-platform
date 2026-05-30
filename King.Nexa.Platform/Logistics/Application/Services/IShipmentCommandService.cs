using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.Commands;

namespace King.Nexa.Platform.Logistics.Application.Services;

public interface IShipmentCommandService
{
    Task<Shipment> ScheduleAsync(ScheduleShipmentCommand command, CancellationToken cancellationToken = default);

    Task<Shipment?> MarkDeliveredAsync(MarkShipmentDeliveredCommand command, CancellationToken cancellationToken = default);
}
