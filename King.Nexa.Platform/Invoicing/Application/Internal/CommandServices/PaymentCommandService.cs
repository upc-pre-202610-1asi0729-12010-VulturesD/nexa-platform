using King.Nexa.Platform.Invoicing.Application.CommandServices;
using King.Nexa.Platform.Invoicing.Domain.Model.Aggregates;
using King.Nexa.Platform.Invoicing.Domain.Model.Commands;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Invoicing.Application.Internal.CommandServices;

public class PaymentCommandService(IPaymentRepository paymentRepository, IUnitOfWork unitOfWork) : IPaymentCommandService
{
    public async Task<Payment> CreateAsync(RegisterPaymentCommand command, CancellationToken cancellationToken = default)
    {
        var payment = new Payment(command);
        await paymentRepository.AddAsync(payment, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return payment;
    }

    public async Task<Payment?> UpdateAsync(UpdatePaymentCommand command, CancellationToken cancellationToken = default)
    {
        var payment = await paymentRepository.FindByIdAsync(command.PaymentId, cancellationToken);
        if (payment is null) return null;

        payment.Update(command);
        paymentRepository.Update(payment);
        await unitOfWork.CompleteAsync(cancellationToken);
        return payment;
    }

    public async Task<bool> DeleteAsync(DeletePaymentCommand command, CancellationToken cancellationToken = default)
    {
        var payment = await paymentRepository.FindByIdAsync(command.PaymentId, cancellationToken);
        if (payment is null) return false;

        payment.Cancel();
        paymentRepository.Update(payment);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
