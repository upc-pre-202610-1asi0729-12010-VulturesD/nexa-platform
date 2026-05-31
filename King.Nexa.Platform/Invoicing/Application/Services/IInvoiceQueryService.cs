using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Queries;

namespace King.Nexa.Platform.Invoicing.Application.Services;

public interface IInvoiceQueryService
{
    Task<IEnumerable<Invoice>> Handle(GetAllInvoicesQuery query, CancellationToken cancellationToken = default);

    Task<Invoice?> Handle(GetInvoiceByIdQuery query, CancellationToken cancellationToken = default);
}
