using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;

namespace King.Nexa.Platform.CatalogManagement.Application.CommandServices;

public interface IBrandCommandService
{
    Task<Brand> CreateAsync(CreateBrandCommand command, CancellationToken cancellationToken = default);

    Task<Brand?> UpdateAsync(UpdateBrandCommand command, CancellationToken cancellationToken = default);

    Task<bool> DeleteAsync(DeleteBrandCommand command, CancellationToken cancellationToken = default);
}
