using King.Nexa.Platform.CatalogManagement.Domain.Model.ValueObjects;

namespace King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

public record CreateBrandCommand(BrandName Name, string Description);
