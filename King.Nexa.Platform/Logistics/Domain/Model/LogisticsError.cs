namespace King.Nexa.Platform.Logistics.Domain.Model;

public enum LogisticsError
{
    None,
    ShipmentNotFound,
    TrackingEventNotFound,
    DeliveryRouteNotFound,
    ShipmentCreationFailed,
    ShipmentUpdateFailed,
    InvalidShipmentStatus,
    InvalidLogisticsData,
    OperationCancelled,
    DatabaseError,
    InternalServerError
}
