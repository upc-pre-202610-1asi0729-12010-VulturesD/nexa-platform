using King.Nexa.Platform.Shared.Domain.Model;

namespace King.Nexa.Platform.Logistics.Domain.Model.Errors;

public static class LogisticsErrors
{
    public static readonly Error ShipmentNotFound =
        new("Logistics.ShipmentNotFound", "The specified shipment was not found.");

    public static readonly Error TrackingEventNotFound =
        new("Logistics.TrackingEventNotFound", "The specified tracking event was not found.");

    public static readonly Error DeliveryRouteNotFound =
        new("Logistics.DeliveryRouteNotFound", "The specified delivery route was not found.");

    public static readonly Error ShipmentCreationFailed =
        new("Logistics.ShipmentCreationFailed", "An error occurred while creating the shipment.");

    public static readonly Error ShipmentUpdateFailed =
        new("Logistics.ShipmentUpdateFailed", "An error occurred while updating the shipment.");

    public static readonly Error InvalidShipmentStatus =
        new("Logistics.InvalidShipmentStatus", "The shipment status is invalid for the requested operation.");

    public static readonly Error InvalidLogisticsData =
        new("Logistics.InvalidLogisticsData", "The supplied logistics data is invalid.");

    public static readonly Error OperationCancelled =
        new("Logistics.OperationCancelled", "The logistics operation was cancelled.");

    public static readonly Error DatabaseError =
        new("Logistics.DatabaseError", "A persistence error occurred while processing logistics data.");

    public static readonly Error InternalServerError =
        new("Logistics.InternalServerError", "An internal server error occurred while processing the logistics request.");
}
