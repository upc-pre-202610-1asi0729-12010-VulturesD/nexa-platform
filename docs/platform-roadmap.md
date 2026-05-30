# Platform Roadmap

## v0.1.0 Backend Foundation

Current milestone:

- establish ASP.NET Core solution and project structure;
- define shared kernel and persistence foundation;
- scaffold Sales, Logistics, Warehouse, Invoicing, and Catalog Management bounded contexts;
- expose lightweight REST controllers for early API shape review;
- document GitFlow and platform scope.

## Next milestones

Planned future work:

- add command and query services for each bounded context;
- add validation and ProblemDetails error responses;
- create EF Core migrations once database decisions are confirmed;
- add integration tests for REST endpoints;
- define authentication and authorization boundaries;
- connect frontend applications only after stable API contracts exist.

## Deployment note

No production deployment is claimed for v0.1.0. Deployment settings, hosting, secrets management, and operational monitoring belong to later platform milestones.
