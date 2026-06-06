using King.Nexa.Platform.Iam.Domain.Model.Aggregates;

namespace King.Nexa.Platform.Iam.Application.Model;

public record AuthenticatedUser(User User, string AccessToken);
