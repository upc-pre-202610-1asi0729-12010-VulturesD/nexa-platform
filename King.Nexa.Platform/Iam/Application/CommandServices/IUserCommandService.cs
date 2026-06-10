using King.Nexa.Platform.Iam.Application.Model;
using King.Nexa.Platform.Iam.Domain.Model.Commands;

namespace King.Nexa.Platform.Iam.Application.CommandServices;

public interface IUserCommandService
{
    Task<AuthenticatedUser> SignUpAsync(SignUpCommand command, CancellationToken cancellationToken = default);

    Task<AuthenticatedUser?> SignInAsync(SignInCommand command, CancellationToken cancellationToken = default);
}
