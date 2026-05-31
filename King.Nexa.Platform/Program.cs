using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.CatalogManagement.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Invoicing.Domain.Repositories;
using King.Nexa.Platform.Invoicing.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Logistics.Domain.Repositories;
using King.Nexa.Platform.Logistics.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Sales.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Shared.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Shared.Infrastructure.Interfaces.ASP.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Interceptors;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EFC.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Infrastructure.Persistence.EFC.Repositories;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddRouting(options => options.LowercaseUrls = true);
builder.Services.AddOpenApi();
builder.Services.AddProblemDetails();

builder.Services.AddScoped<AuditableEntityInterceptor>();
builder.Services.AddDbContext<AppDbContext>((serviceProvider, options) =>
{
    var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
    if (string.IsNullOrWhiteSpace(connectionString))
        throw new InvalidOperationException("DefaultConnection is not configured.");

    options.UseSqlServer(connectionString)
        .AddInterceptors(serviceProvider.GetRequiredService<AuditableEntityInterceptor>());
});

builder.Services.AddScoped(typeof(IBaseRepository<>), typeof(BaseRepository<>));
builder.Services.AddScoped<IUnitOfWork, UnitOfWork>();
builder.Services.AddScoped<IOrderRepository, OrderRepository>();
builder.Services.AddScoped<IShipmentRepository, ShipmentRepository>();
builder.Services.AddScoped<IInventoryItemRepository, InventoryItemRepository>();
builder.Services.AddScoped<IInvoiceRepository, InvoiceRepository>();
builder.Services.AddScoped<IProductRepository, ProductRepository>();
builder.Services.AddPlatformApplicationServices();

builder.Services.AddControllers(options =>
{
    options.Conventions.Add(new KebabCaseRouteNamingConvention());
});

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}
else
{
    app.UseExceptionHandler();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
