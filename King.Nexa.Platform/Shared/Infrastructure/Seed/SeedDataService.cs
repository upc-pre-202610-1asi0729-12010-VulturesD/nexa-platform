using System.Text.Json;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Iam.Application.OutboundServices;
using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Iam.Domain.Repositories;
using King.Nexa.Platform.Sales.Domain.Model.Aggregates;
using King.Nexa.Platform.Sales.Domain.Model.Commands;
using King.Nexa.Platform.Sales.Domain.Model.ValueObjects;
using King.Nexa.Platform.Sales.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;
using King.Nexa.Platform.Warehouse.Domain.Model.Aggregates;
using King.Nexa.Platform.Warehouse.Domain.Model.Commands;
using King.Nexa.Platform.Warehouse.Domain.Repositories;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Options;
using CatalogBrandName = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.BrandName;
using CatalogCatalogItemId = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.CatalogItemId;
using CatalogCategoryName = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.CategoryName;
using CatalogColdChainRequirement = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.ColdChainRequirement;
using CatalogItemName = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.ItemName;
using CatalogMoney = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.Money;
using CatalogProductId = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.ProductId;
using CatalogStockQuantity = King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects.StockQuantity;
using WarehouseCatalogItemId = King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects.CatalogItemId;
using WarehouseProductId = King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects.ProductId;
using WarehouseStockQuantity = King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects.StockQuantity;
using WarehouseTemperatureRange = King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects.TemperatureRange;
using WarehouseLocation = King.Nexa.Platform.Warehouse.Domain.Model.ValueObjects.WarehouseLocation;

namespace King.Nexa.Platform.Shared.Infrastructure.Seed;

public class SeedDataService(
    IWebHostEnvironment environment,
    IOptions<SeedDataOptions> options,
    ICatalogItemRepository catalogItemRepository,
    IInventoryItemRepository inventoryItemRepository,
    IUserRepository userRepository,
    IOrderRepository orderRepository,
    IPasswordHashingService passwordHashingService,
    IUnitOfWork unitOfWork) : ISeedDataService
{
    private static readonly JsonSerializerOptions JsonOptions = new(JsonSerializerDefaults.Web);

    public async Task SeedAsync(CancellationToken cancellationToken = default)
    {
        if (!options.Value.Enabled) return;

        await SeedCatalogItemsAsync(cancellationToken);
        await SeedInventoryItemsAsync(cancellationToken);
        await SeedUsersAsync(cancellationToken);
        await SeedOrdersAsync(cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
    }

    private async Task SeedCatalogItemsAsync(CancellationToken cancellationToken)
    {
        var records = await ReadSeedFileAsync<CatalogItemSeedRecord>("catalog-items.seed.json", cancellationToken);
        foreach (var record in records)
        {
            var catalogItemId = new CatalogCatalogItemId(record.CatalogItemId);
            if (await catalogItemRepository.FindByCatalogItemIdAsync(catalogItemId, cancellationToken) is not null)
                continue;

            var command = new CreateCatalogItemCommand(
                catalogItemId,
                new CatalogProductId(record.ProductId),
                new CatalogItemName(record.ItemName),
                new CatalogBrandName(record.BrandName),
                new CatalogCategoryName(record.CategoryName),
                record.Description,
                record.ImageUrl,
                new CatalogMoney(record.UnitPriceAmount, record.UnitPriceCurrency),
                new CatalogStockQuantity(record.AvailableStock),
                Enum.Parse<CatalogColdChainRequirement>(record.ColdChainRequirement, true));

            await catalogItemRepository.AddAsync(new CatalogItem(command), cancellationToken);
        }
    }

    private async Task SeedInventoryItemsAsync(CancellationToken cancellationToken)
    {
        var records = await ReadSeedFileAsync<InventoryItemSeedRecord>("inventory-items.seed.json", cancellationToken);
        foreach (var record in records)
        {
            var catalogItemId = new WarehouseCatalogItemId(record.CatalogItemId);
            if (await inventoryItemRepository.FindByCatalogItemIdAsync(catalogItemId, cancellationToken) is not null)
                continue;

            var command = new CreateInventoryItemCommand(
                new WarehouseProductId(record.ProductId),
                catalogItemId,
                new WarehouseStockQuantity(record.AvailableQuantity),
                new WarehouseLocation(record.WarehouseLocation),
                new WarehouseTemperatureRange(record.MinimumTemperature, record.MaximumTemperature));

            await inventoryItemRepository.AddAsync(new InventoryItem(command), cancellationToken);
        }
    }

    private async Task SeedUsersAsync(CancellationToken cancellationToken)
    {
        var records = await ReadSeedFileAsync<UserSeedRecord>("users.seed.json", cancellationToken);
        
        var allUsers = await userRepository.ListAsync(cancellationToken);
        foreach (var user in allUsers)
        {
            if (!records.Any(r => r.Username.Equals(user.Username, StringComparison.OrdinalIgnoreCase)))
            {
                userRepository.Remove(user);
            }
        }

        foreach (var record in records)
        {
            var existingUser = await userRepository.FindByUsernameAsync(record.Username, cancellationToken);
            if (existingUser is not null)
            {
                if (existingUser.Role != record.Role || existingUser.Email != record.Email)
                {
                    userRepository.Remove(existingUser);
                    var passwordHash = passwordHashingService.HashPassword(record.Password);
                    await userRepository.AddAsync(new User(record.Username, record.Email, passwordHash, record.Role), cancellationToken);
                }
            }
            else
            {
                var passwordHash = passwordHashingService.HashPassword(record.Password);
                await userRepository.AddAsync(new User(record.Username, record.Email, passwordHash, record.Role), cancellationToken);
            }
        }
    }

    private async Task SeedOrdersAsync(CancellationToken cancellationToken)
    {
        var records = await ReadSeedFileAsync<OrderSeedRecord>("orders.seed.json", cancellationToken);
        foreach (var record in records)
        {
            var orderNumber = new OrderNumber(record.OrderNumber);
            if (await orderRepository.FindByOrderNumberAsync(orderNumber, cancellationToken) is not null)
                continue;

            var order = new Order(new CreateOrderCommand(
                orderNumber,
                new CustomerId(record.CustomerId),
                record.Items.Select(ToCommand).ToList()));

            ApplyOrderStatus(order, record);
            await orderRepository.AddAsync(order, cancellationToken);
        }
    }

    private static CreateOrderItemCommand ToCommand(OrderItemSeedRecord record) =>
        new(
            new ProductId(record.ProductId),
            new CatalogItemId(record.CatalogItemId),
            new ItemName(record.ItemName),
            new Quantity(record.Quantity),
            new Money(record.UnitPriceAmount, record.UnitPriceCurrency));

    private static void ApplyOrderStatus(Order order, OrderSeedRecord record)
    {
        var status = Enum.Parse<OrderStatus>(record.Status, true);
        switch (status)
        {
            case OrderStatus.Pending:
                return;
            case OrderStatus.Confirmed:
                order.Confirm(new PaymentConfirmation(record.PaymentConfirmation!), new InventoryReservation(record.InventoryReservation!));
                return;
            case OrderStatus.Rejected:
                order.Reject(new RejectionReason(record.RejectionReason!));
                return;
            case OrderStatus.Cancelled:
                order.Cancel();
                return;
            case OrderStatus.Paid:
            default:
                throw new InvalidOperationException($"Unsupported seed order status '{record.Status}'.");
        }
    }

    private async Task<IReadOnlyCollection<T>> ReadSeedFileAsync<T>(string fileName, CancellationToken cancellationToken)
    {
        var path = Path.Combine(environment.ContentRootPath, "Resources", "SeedData", fileName);
        if (!File.Exists(path)) return [];

        await using var stream = File.OpenRead(path);
        return await JsonSerializer.DeserializeAsync<IReadOnlyCollection<T>>(stream, JsonOptions, cancellationToken) ?? [];
    }

    private sealed record CatalogItemSeedRecord(
        string CatalogItemId,
        string ProductId,
        string ItemName,
        string BrandName,
        string CategoryName,
        string Description,
        string ImageUrl,
        decimal UnitPriceAmount,
        string UnitPriceCurrency,
        int AvailableStock,
        string ColdChainRequirement);

    private sealed record InventoryItemSeedRecord(
        string ProductId,
        string CatalogItemId,
        int AvailableQuantity,
        string WarehouseLocation,
        decimal MinimumTemperature,
        decimal MaximumTemperature);

    private sealed record UserSeedRecord(string Username, string Email, string Password, string Role);

    private sealed record OrderSeedRecord(
        string OrderNumber,
        string CustomerId,
        string CustomerName,
        string Status,
        IReadOnlyCollection<OrderItemSeedRecord> Items,
        string? PaymentConfirmation,
        string? InventoryReservation,
        string? RejectionReason);

    private sealed record OrderItemSeedRecord(
        string ProductId,
        string CatalogItemId,
        string ItemName,
        int Quantity,
        decimal UnitPriceAmount,
        string UnitPriceCurrency);
}
