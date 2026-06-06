namespace King.Nexa.Platform.Shared.Application.Model;

public sealed record PaginatedResult<T>(IReadOnlyCollection<T> Items, int Page, int PageSize, int TotalItems)
{
    public int TotalPages => PageSize <= 0 ? 0 : (int)Math.Ceiling((double)TotalItems / PageSize);
}
