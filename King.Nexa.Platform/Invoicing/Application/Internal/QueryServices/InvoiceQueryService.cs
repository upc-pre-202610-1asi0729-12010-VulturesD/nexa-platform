using King.Nexa.Platform.Invoicing.Application.CommandServices;
using King.Nexa.Platform.Invoicing.Application.QueryServices;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;
using King.Nexa.Platform.Invoicing.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Application.Internal.QueryServices;

public class InvoiceQueryService(IInvoiceRepository invoiceRepository) : IInvoiceQueryService
{
    public async Task<IEnumerable<Invoice>> Handle(GetAllInvoicesQuery query, CancellationToken cancellationToken = default) =>
        await invoiceRepository.ListAsync(cancellationToken);

    public async Task<Invoice?> Handle(GetInvoiceByIdQuery query, CancellationToken cancellationToken = default) =>
        await invoiceRepository.FindByIdAsync(query.InvoiceId, cancellationToken);

    public async Task<IEnumerable<Invoice>> Handle(GetInvoicesByOrderIdQuery query, CancellationToken cancellationToken = default) =>
        await invoiceRepository.ListByOrderIdAsync(query.OrderId, cancellationToken);

    public async Task<IEnumerable<Invoice>> Handle(GetInvoicesByPaymentStatusQuery query, CancellationToken cancellationToken = default) =>
        await invoiceRepository.ListByPaymentStatusAsync(query.PaymentStatus, cancellationToken);
}
