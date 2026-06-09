using King.Nexa.Platform.Invoicing.Application.QueryServices;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;
using King.Nexa.Platform.Invoicing.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Application.Internal.QueryServices;

public class PaymentQueryService(IPaymentRepository paymentRepository) : IPaymentQueryService
{
    public async Task<IEnumerable<Payment>> Handle(GetAllPaymentsQuery query, CancellationToken cancellationToken = default) =>
        await paymentRepository.ListAsync(cancellationToken);

    public async Task<Payment?> Handle(GetPaymentByIdQuery query, CancellationToken cancellationToken = default) =>
        await paymentRepository.FindByIdAsync(query.PaymentId, cancellationToken);

    public async Task<IEnumerable<Payment>> Handle(GetPaymentsByInvoiceIdQuery query, CancellationToken cancellationToken = default) =>
        await paymentRepository.ListByInvoiceIdAsync(query.InvoiceId, cancellationToken);

    public async Task<IEnumerable<Payment>> Handle(GetPaymentsByStatusQuery query, CancellationToken cancellationToken = default) =>
        await paymentRepository.ListByStatusAsync(query.Status, cancellationToken);
}
