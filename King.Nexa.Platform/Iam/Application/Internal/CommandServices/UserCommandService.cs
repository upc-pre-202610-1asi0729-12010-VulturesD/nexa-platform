using King.Nexa.Platform.Iam.Application.CommandServices;
using King.Nexa.Platform.Iam.Application.Model;
using King.Nexa.Platform.Iam.Application.OutboundServices;
using King.Nexa.Platform.Iam.Domain.Model.Aggregates;
using King.Nexa.Platform.Iam.Domain.Model.Commands;
using King.Nexa.Platform.Iam.Domain.Repositories;
using King.Nexa.Platform.Shared.Domain.Repositories;

namespace King.Nexa.Platform.Iam.Application.Internal.CommandServices;

public class UserCommandService(
    IUserRepository userRepository,
    IUnitOfWork unitOfWork,
    IPasswordHashingService passwordHashingService,
    ITokenService tokenService) : IUserCommandService
{
    public async Task<AuthenticatedUser> SignUpAsync(SignUpCommand command, CancellationToken cancellationToken = default)
    {
        var existingUser = await userRepository.FindByUsernameAsync(command.Username, cancellationToken);
        if (existingUser is not null)
            throw new InvalidOperationException("Username is already registered.");

        var user = new User(command.Username, command.Email, passwordHashingService.HashPassword(command.Password), command.Role);
        await userRepository.AddAsync(user, cancellationToken);
        await unitOfWork.CompleteAsync(cancellationToken);

        return new AuthenticatedUser(user, tokenService.GenerateToken(user));
    }

    public async Task<AuthenticatedUser?> SignInAsync(SignInCommand command, CancellationToken cancellationToken = default)
    {
        var user = await userRepository.FindByUsernameAsync(command.Username, cancellationToken);
        if (user is null || !passwordHashingService.VerifyPassword(command.Password, user.PasswordHash))
            return null;

        return new AuthenticatedUser(user, tokenService.GenerateToken(user));
    }
}
