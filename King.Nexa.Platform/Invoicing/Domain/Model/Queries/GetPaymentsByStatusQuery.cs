using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.Invoicing.Domain.Model.Queries;

public record GetPaymentsByStatusQuery(PaymentStatus Status);
