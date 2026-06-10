using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Commands;

namespace King.Nexa.Platform.Invoicing.Application.CommandServices;

public interface IInvoiceCommandService
{
    Task<Invoice> GenerateAsync(GenerateInvoiceCommand command, CancellationToken cancellationToken = default);

    Task<Invoice?> UpdateAsync(UpdateInvoiceCommand command, CancellationToken cancellationToken = default);

    Task<Invoice?> MarkPaidAsync(MarkInvoicePaidCommand command, CancellationToken cancellationToken = default);

    Task<bool> CancelAsync(CancelInvoiceCommand command, CancellationToken cancellationToken = default);
}
