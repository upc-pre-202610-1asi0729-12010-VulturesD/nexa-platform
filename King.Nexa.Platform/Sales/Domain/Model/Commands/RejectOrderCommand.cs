using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Sales.Domain.Model.Commands;

public record RejectOrderCommand(int OrderId, RejectionReason RejectionReason);
