using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Domain.Repositories;

public interface IPaymentRepository : IBaseRepository<Payment>
{
    Task<IEnumerable<Payment>> ListByInvoiceIdAsync(int invoiceId, CancellationToken cancellationToken = default);

    Task<IEnumerable<Payment>> ListByStatusAsync(PaymentStatus status, CancellationToken cancellationToken = default);
}
