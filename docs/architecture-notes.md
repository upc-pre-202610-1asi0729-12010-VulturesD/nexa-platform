# Nexa Platform Architecture Notes

`nexa-platform` is the planned backend service layer for the Nexa B2B platform. The current repository establishes a small ASP.NET Core foundation that can grow into the service layer for later milestones.

## Application style

- ASP.NET Core Web API with controller-based REST endpoints.
- C# project under `King.Nexa.Platform`.
- Entity Framework Core is prepared as the persistence technology.
- REST routes use `/api/v1` and plural resource names.
- Source folders follow bounded context boundaries instead of generic technical buckets.

## Layering

Each bounded context keeps the same high-level shape:

- `Domain`: aggregates, value objects, commands, queries, and repository contracts.
- `Application`: command and query service contracts plus lightweight internal services that coordinate domain operations.
- `Infrastructure`: EF Core repositories and persistence details.
- `Interfaces`: REST resources, assemblers, and controllers.

## Application layer

The application layer keeps controllers thin without pretending the platform has a completed business workflow. Each bounded context currently exposes:

- command service contracts for write operations;
- query service contracts for read operations;
- internal command/query service implementations that coordinate repositories and the unit of work.

This gives later milestones a clear place to add validation, policies, and result handling without putting those concerns in controllers.

## Shared kernel

The shared kernel currently provides:

- base entity and auditable entity contracts;
- generic repository and unit of work contracts;
- EF Core base repository implementation;
- `AppDbContext` foundation;
- audit timestamp interceptor;
- kebab-case route naming convention.

This is intentionally a foundation. It is not a deployed production backend.
