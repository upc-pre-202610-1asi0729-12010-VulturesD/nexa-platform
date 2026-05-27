namespace King.Nexa.Platform.Shared.Domain.Model;

public abstract class AuditableEntity : Entity, IAuditableEntity
{
    public DateTimeOffset CreatedAt { get; set; }

    public DateTimeOffset? UpdatedAt { get; set; }
}
