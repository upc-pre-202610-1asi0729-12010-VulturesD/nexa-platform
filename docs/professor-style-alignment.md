# Professor-Style Architecture Alignment

## Reference Use

The professor CatchUp project was used as an architecture and package-style reference only. Nexa keeps its own cold-chain B2B domain, bounded contexts, endpoints, and product behavior.

## Applied Patterns

- Bounded contexts remain under `com.nexa.platform.<context>`.
- Catalog now has `application/commands`, `application/queries`, `application/commandservices`, and `application/queryservices`.
- Catalog public API contracts now live under `interfaces/rest/resources`.
- Catalog resource assemblers now live under `interfaces/rest/transform`.
- Shared persistence contains a snake_case physical naming strategy.
- REST controllers keep status codes, validation annotations, and exception handling through shared REST errors.

## Deliberate Boundaries

- Existing domain models are still JPA entities to avoid a high-risk rewrite during final recovery.
- Persistence entity separation can be introduced later through adapters and mappers per bounded context.
- Current alignment prioritizes course-visible package structure, stable API behavior, tests, MySQL readiness, and WebApp compatibility.

## OpenAPI and Swagger Readiness

Springdoc generates OpenAPI from controllers and resource records. Swagger UI remains available at `/swagger-ui/index.html`, and the OpenAPI document remains available at `/v3/api-docs`.
