using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class InvoiceRepository(AppDbContext context) : BaseRepository<Invoice>(context), IInvoiceRepository
{
    public async Task<Invoice?> FindByInvoiceNumberAsync(InvoiceNumber invoiceNumber, CancellationToken cancellationToken = default) =>
        await Context.Invoices.FirstOrDefaultAsync(invoice => invoice.InvoiceNumber == invoiceNumber, cancellationToken);
}
