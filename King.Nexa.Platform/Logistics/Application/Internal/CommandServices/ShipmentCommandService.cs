using King.Nexa.Platform.Logistics.Application.CommandServices;
using King.Nexa.Platform.Logistics.Application.QueryServices;
using King.Nexa.Platform.Logistics.Domain.Model.Aggregates;
using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Logistics.Application.Internal.CommandServices;

public class ShipmentCommandService(IShipmentRepository shipmentRepository, IUnitOfWork unitOfWork) : IShipmentCommandService
{
    public async Task<Shipment> ScheduleAsync(ScheduleShipmentCommand command, CancellationToken cancellationToken = default)
    {
        var shipment = new Shipment(command);
        await shipmentRepository.AddAsync(shipment, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return shipment;
    }

    public async Task<Shipment?> RescheduleAsync(RescheduleShipmentCommand command, CancellationToken cancellationToken = default)
    {
        var shipment = await shipmentRepository.FindByIdAsync(command.ShipmentId, cancellationToken);
        if (shipment is null) return null;

        shipment.Reschedule(command);
        shipmentRepository.Update(shipment);
        await unitOfWork.CompleteAsync(cancellationToken);
        return shipment;
    }

    public async Task<Shipment?> MarkDeliveredAsync(MarkShipmentDeliveredCommand command, CancellationToken cancellationToken = default)
    {
        var shipment = await shipmentRepository.FindByIdAsync(command.ShipmentId, cancellationToken);
        if (shipment is null) return null;

        shipment.MarkDelivered();
        shipmentRepository.Update(shipment);
        await unitOfWork.CompleteAsync(cancellationToken);
        return shipment;
    }

    public async Task<bool> CancelAsync(CancelShipmentCommand command, CancellationToken cancellationToken = default)
    {
        var shipment = await shipmentRepository.FindByIdAsync(command.ShipmentId, cancellationToken);
        if (shipment is null) return false;

        shipment.Cancel();
        shipmentRepository.Update(shipment);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
