# Contributing to Nexa Platform

Thank you for your interest in contributing to Nexa Platform! This document outlines the standards, guidelines, and workflows for contributing to our C#/.NET Clean Architecture and Domain-Driven Design (DDD) codebase.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md) at all times.

## Architectural Guidelines

The Nexa Platform backend is built using a **Modular Monolith** pattern following **Clean Architecture** and **DDD** principles. When adding or modifying code, please respect the boundaries of each Bounded Context:

1. **Domain Layer:**
   - Aggregates must not be anemic. Encapsulate business logic and validate domain invariants within aggregate methods using private/protected setters.
   - Separate audit fields (`CreatedAt`, `UpdatedAt`) using partial classes implementing `IAuditableEntity` (e.g. `OrderAudit.cs` for the `Order` aggregate).
   - Implement Value Objects as immutable `record` types and perform internal validation in their constructors, throwing `ArgumentException` for invalid states.
   
2. **Application Layer:**
   - Orchestrate use cases using Command and Query Services.
   - Use the `Result` pattern to return success or error states instead of throwing business exceptions for control flow.

3. **Infrastructure Layer:**
   - Confine all database/EF Core mapping, queries, and migrations details to this layer.
   - Follow pluralized `snake_case` naming conventions for tables and columns in PostgreSQL.

4. **Interfaces Layer:**
   - Keep REST API controllers thin. Use resources/DTOs for contracts, and translate them to Domain models using Assemblers.
   - Return RFC 7807 `ProblemDetails` format for error responses.

## Development Workflow

### Setup Local Environment
For instructions on setting up your local PostgreSQL database, environment variables, and running the application, see [Environment Setup](docs/database-setup.md).

### Branch Naming Conventions
Always create a new branch from `develop` using one of the following prefixes:
- `feature/` for new functionality (e.g. `feature/low-stock-alert`)
- `fix/` for bug fixes (e.g. `fix/jwt-expiration-check`)
- `docs/` for documentation updates (e.g. `docs/api-routes`)
- `refactor/` for code restructuring (e.g. `refactor/order-aggregate`)

### Commits Convention
We follow Conventional Commits format:
- `feat(sales): add order cancellation endpoint`
- `fix(iam): resolve password hashing salt issues`
- `docs(wiki): update local deployment steps`

## Pull Request Process

1. Ensure your code compiles cleanly using `dotnet build` with zero errors.
2. Run database migrations locally and check database counts.
3. Exclude any local credentials (`appsettings.Local.json`, `.env`) from version control.
4. Open a pull request against the `develop` branch.
5. Provide a summary of the changes and local testing results (e.g. Swagger queries).
6. Request a review from at least one backend team member.
