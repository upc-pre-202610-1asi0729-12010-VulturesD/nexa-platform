using King.Nexa.Platform.CatalogManagement.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Iam.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Invoicing.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Logistics.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Sales.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Shared.Infrastructure.DependencyInjection;
using King.Nexa.Platform.Shared.Infrastructure.Persistence.EntityFrameworkCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Interfaces.AspNetCore.Configuration;
using King.Nexa.Platform.Shared.Infrastructure.Pipeline.Middleware.Extensions;
using King.Nexa.Platform.Shared.Infrastructure.Seed;
using King.Nexa.Platform.Warehouse.Infrastructure.DependencyInjection;
using Microsoft.EntityFrameworkCore;
using System.Reflection;

var builder = WebApplication.CreateBuilder(args);
builder.Configuration.AddJsonFile("appsettings.Local.json", optional: true, reloadOnChange: true);

const string frontendCorsPolicy = "AllowFrontend";

builder.Services.AddRouting(options => options.LowercaseUrls = true);
builder.Services.AddCors(options =>
{
    options.AddPolicy(frontendCorsPolicy, policy =>
    {
        policy
            .WithOrigins("http://localhost:5173", "https://localhost:5173")
            .AllowAnyHeader()
            .AllowAnyMethod();
    });
});

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

// Configure Database Context and route EF logs through the app logger pipeline.
using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    var logger = services.GetRequiredService<ILogger<Program>>();

    try
    {
        var context = services.GetRequiredService<AppDbContext>();
        await context.Database.MigrateAsync();
        logger.LogInformation("Database migrations applied successfully.");

        if (app.Environment.IsDevelopment())
        {
            var seedDataService = services.GetRequiredService<ISeedDataService>();
            await seedDataService.SeedAsync();
        }
    }
    catch (Exception ex)
    {
        logger.LogError(ex, "An error occurred while applying database migrations.");
        throw;
    }
}

app.UseGlobalExceptionHandling();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseCors(frontendCorsPolicy);
app.UseAuthorization();
app.MapControllers();

app.Run();
