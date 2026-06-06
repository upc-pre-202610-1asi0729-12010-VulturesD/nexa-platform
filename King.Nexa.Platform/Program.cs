using King.Nexa.Platform.CatalogManagement.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Iam.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Invoicing.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Logistics.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Sales.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Shared.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Shared.Infrastructure.Interfaces.AspNetCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Extensions;
using King.Nexa.Platform.Warehouse.Infrastructure.DependencyInjection;
using System.Reflection;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddRouting(options => options.LowercaseUrls = true);

builder.Services.AddSharedInfrastructure(builder.Configuration);
builder.Services.AddCatalogManagement();
builder.Services.AddSales();
builder.Services.AddWarehouse();
builder.Services.AddLogistics();
builder.Services.AddInvoicing();
builder.Services.AddIam();

builder.Services.AddControllers(options =>
{
    options.Conventions.Add(new KebabCaseRouteNamingConvention());
});
builder.Services.AddLocalization();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);

    if (File.Exists(xmlPath))
        options.IncludeXmlComments(xmlPath);
});

var app = builder.Build();

app.UseGlobalExceptionHandling();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();
