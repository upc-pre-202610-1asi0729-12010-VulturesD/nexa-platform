# Contributing to Nexa Platform

This repository contains the Java Spring Boot REST API for Nexa.

## Architecture Rules

- Preserve bounded contexts: IAM, Catalog, Sales, Warehouse, Logistics, Invoicing, Promotions, Shared.
- Keep domain models free of Spring MVC, HTTP, persistence annotations beyond existing JPA entities, and presentation concerns.
- Keep controllers thin; use resources/DTOs and assemblers where the context already follows that pattern.
- Keep external communication and Spring Data JPA adapters in infrastructure.
- Do not expose credentials or commit `.env` files.
- Do not change public `/api/v1` endpoint names without a clear migration reason.

## Local Validation

Before opening a pull request:

```bash
./mvnw test
./mvnw package
```

For local API verification:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Branches And Commits

- Use GitFlow-style branches: `feature/*`, `fix/*`, `docs/*`, `refactor/*`, `chore/*`.
- Use Conventional Commits when possible:
  - `feat(catalog): add product query endpoint`
  - `fix(iam): correct JWT validation`
  - `docs(deploy): update Render environment variables`

## Pull Request Checklist

- [ ] Tests pass with `./mvnw test`.
- [ ] Package succeeds with `./mvnw package`.
- [ ] No generated files such as `target/` are committed.
- [ ] No secrets or credentials are committed.
- [ ] README/docs are updated when behavior, deployment, or API contracts change.
