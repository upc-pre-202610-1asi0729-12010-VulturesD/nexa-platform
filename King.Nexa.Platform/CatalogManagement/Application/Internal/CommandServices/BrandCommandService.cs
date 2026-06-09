using King.Nexa.Platform.CatalogManagement.Application.CommandServices;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Aggregates;
using King.Nexa.Platform.CatalogManagement.Domain.Model.Commands;
using King.Nexa.Platform.CatalogManagement.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.CatalogManagement.Application.Internal.CommandServices;

public class BrandCommandService(IBrandRepository brandRepository, IUnitOfWork unitOfWork) : IBrandCommandService
{
    public async Task<Brand> CreateAsync(CreateBrandCommand command, CancellationToken cancellationToken = default)
    {
        var brand = new Brand(command);
        await brandRepository.AddAsync(brand, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);
        return brand;
    }

    public async Task<Brand?> UpdateAsync(UpdateBrandCommand command, CancellationToken cancellationToken = default)
    {
        var brand = await brandRepository.FindByIdAsync(command.BrandId, cancellationToken);
        if (brand is null) return null;

        brand.Update(command);
        brandRepository.Update(brand);
        await unitOfWork.CompleteAsync(cancellationToken);
        return brand;
    }

    public async Task<bool> DeleteAsync(DeleteBrandCommand command, CancellationToken cancellationToken = default)
    {
        var brand = await brandRepository.FindByIdAsync(command.BrandId, cancellationToken);
        if (brand is null) return false;

        brand.Deactivate();
        brandRepository.Update(brand);
        await unitOfWork.CompleteAsync(cancellationToken);
        return true;
    }
}
