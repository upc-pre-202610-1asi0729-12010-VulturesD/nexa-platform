using King.Nexa.Platform.Invoicing.Application.Services;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Application.Internal.CommandServices;

public class InvoiceCommandService(IInvoiceRepository invoiceRepository, IUnitOfWork unitOfWork) : IInvoiceCommandService
{
    public async Task<Invoice> GenerateAsync(GenerateInvoiceCommand command, CancellationToken cancellationToken = default)
    {
        var invoice = new Invoice(command);
        await invoiceRepository.AddAsync(invoice, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return invoice;
    }

    public async Task<Invoice?> MarkPaidAsync(MarkInvoicePaidCommand command, CancellationToken cancellationToken = default)
    {
        var invoice = await invoiceRepository.FindByIdAsync(command.InvoiceId, cancellationToken);
        if (invoice is null) return null;

        invoice.MarkPaid();
        invoiceRepository.Update(invoice);
        await unitOfWork.CompleteAsync(cancellationToken);
        return invoice;
    }
}
