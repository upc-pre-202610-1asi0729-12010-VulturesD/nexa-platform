using King.Nexa.Platform.Logistics.Domain.Model.Commands;
using King.Nexa.Platform.Logistics.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Model.Entities;

namespace King.Nexa.Platform.Logistics.Domain.Model.Aggregates;

public class Shipment : AuditableEntity
{
    protected Shipment()
    {
        ShipmentCode = null!;
    }

    public Shipment(ScheduleShipmentCommand command)
    {
        ShipmentCode = command.ShipmentCode;
        OrderId = command.OrderId;
        ScheduledAt = command.ScheduledAt;
        Status = DeliveryStatus.Scheduled;
    }

    public ShipmentCode ShipmentCode { get; private set; }

    public int OrderId { get; private set; }

    public DateTimeOffset ScheduledAt { get; private set; }

    public DateTimeOffset? DeliveredAt { get; private set; }

    public DeliveryStatus Status { get; private set; }

    public TemperatureRecord? LastTemperatureRecord { get; private set; }

    public void RegisterTemperature(decimal celsius) => LastTemperatureRecord = new TemperatureRecord(celsius);

    public void MarkDelivered()
    {
        Status = DeliveryStatus.Delivered;
        DeliveredAt = DateTimeOffset.UtcNow;
    }
}
