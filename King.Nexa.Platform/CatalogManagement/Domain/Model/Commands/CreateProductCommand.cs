using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record CreateProductCommand(ProductCode ProductCode, string Name, CategoryName CategoryName, ColdChainRequirement ColdChainRequirement);
