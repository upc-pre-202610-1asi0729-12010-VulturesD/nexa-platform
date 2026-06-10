using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;

namespace King.Nexa.Platform.Invoicing.Application.QueryServices;

public interface IInvoiceQueryService
{
    Task<IEnumerable<Invoice>> Handle(GetAllInvoicesQuery query, CancellationToken cancellationToken = default);

    Task<Invoice?> Handle(GetInvoiceByIdQuery query, CancellationToken cancellationToken = default);

    Task<IEnumerable<Invoice>> Handle(GetInvoicesByOrderIdQuery query, CancellationToken cancellationToken = default);

    Task<IEnumerable<Invoice>> Handle(GetInvoicesByPaymentStatusQuery query, CancellationToken cancellationToken = default);
}
