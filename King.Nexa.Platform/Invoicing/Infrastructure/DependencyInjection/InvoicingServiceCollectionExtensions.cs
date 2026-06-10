using King.Nexa.Platform.Invoicing.Application.Internal.CommandServices;
using King.Nexa.Platform.Invoicing.Application.Internal.QueryServices;
using King.Nexa.Platform.Invoicing.Application.CommandServices;
using King.Nexa.Platform.Invoicing.Application.QueryServices;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EntityFrameworkCore.Repositories;

namespace King.Nexa.Platform.Invoicing.Infrastructure.DependencyInjection;

public static class InvoicingServiceCollectionExtensions
{
    public static IServiceCollection AddInvoicing(this IServiceCollection services)
    {
        services.AddScoped<IInvoiceRepository, InvoiceRepository>();
        services.AddScoped<IInvoiceCommandService, InvoiceCommandService>();
        services.AddScoped<IInvoiceQueryService, InvoiceQueryService>();
        services.AddScoped<IPaymentRepository, PaymentRepository>();
        services.AddScoped<IPaymentCommandService, PaymentCommandService>();
        services.AddScoped<IPaymentQueryService, PaymentQueryService>();

        return services;
    }
}
