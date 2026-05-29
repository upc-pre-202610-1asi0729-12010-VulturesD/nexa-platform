using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record UpdateProductCommand(int ProductId, string Name, CategoryName CategoryName, ColdChainRequirement ColdChainRequirement);
