using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Domain.Repositories;

public interface IInvoiceRepository : IBaseRepository<Invoice>
{
    Task<Invoice?> FindByInvoiceNumberAsync(InvoiceNumber invoiceNumber, CancellationToken cancellationToken = default);

    Task<IEnumerable<Invoice>> ListByOrderIdAsync(int orderId, CancellationToken cancellationToken = default);

    Task<IEnumerable<Invoice>> ListByPaymentStatusAsync(PaymentStatus paymentStatus, CancellationToken cancellationToken = default);
}
