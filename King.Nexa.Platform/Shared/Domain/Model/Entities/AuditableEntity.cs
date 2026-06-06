namespace King.Nexa.Platform.Shared.Domain.Model.Entities;

public abstract class AuditableEntity : Entity, IAuditableEntity
{
    public DateTime CreatedAt { get; set; }

    public DateTime? UpdatedAt { get; set; }
}
