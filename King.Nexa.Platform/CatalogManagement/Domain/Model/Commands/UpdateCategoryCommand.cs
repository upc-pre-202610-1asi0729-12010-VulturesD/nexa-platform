using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record UpdateCategoryCommand(int CategoryId, CategoryName Name, string Description);
