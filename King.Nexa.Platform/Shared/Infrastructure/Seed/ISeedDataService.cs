namespace King.Nexa.Platform.Shared.Infrastructure.Seed;

public interface ISeedDataService
{
    Task SeedAsync(CancellationToken cancellationToken = default);
}
