using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.ValueObjects;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Repositories;
using Microsoft.EntityFrameworkCore;

namespace King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

public class PaymentRepository(AppDbContext context) : BaseRepository<Payment>(context), IPaymentRepository
{
    public async Task<IEnumerable<Payment>> ListByInvoiceIdAsync(int invoiceId, CancellationToken cancellationToken = default) =>
        await Context.Payments.Where(payment => payment.InvoiceId == invoiceId).ToListAsync(cancellationToken);

    public async Task<IEnumerable<Payment>> ListByStatusAsync(PaymentStatus status, CancellationToken cancellationToken = default) =>
        await Context.Payments.Where(payment => payment.Status == status).ToListAsync(cancellationToken);
}
