# Nexa Platform Architecture

The API is organized around bounded contexts. Each context owns its domain model, application services, infrastructure adapters, and REST interfaces. Shared code is restricted to cross-cutting concerns such as security configuration, OpenAPI metadata, error handling, CORS, and bootstrap data.

## Context Map

- IAM authenticates operators and exposes the current user profile.
- Catalog Management owns product and category reference data.
- Warehouse owns stock state, warehouses, inventory movements, and alerts.
- Sales owns customers and order lifecycle.
- Logistics owns shipments, delivery routes, tracking, and driver checklist data.
- Invoicing owns invoices, business documents, and payments.
- Promotions owns commercial promotion records.

## Persistence

The `postgres` profile uses PostgreSQL through environment variables and Spring Data JPA mappings. The `local` and `test` profiles use H2 for fast verification. The optional `seed` profile loads idempotent academic demo data so Render review environments have users, catalog items, customers, orders, inventory, documents, payments, routes, and promotions.

## Professor-Style Layers

The platform follows the course reference through visible bounded-context layers:

```text
com.nexa.platform.<context>
  domain/model
  domain/model/repositories
  application/commands
  application/queries
  application/commandservices
  application/queryservices
  application/internal
  infrastructure/persistence/jpa
  interfaces/rest/resources
  interfaces/rest/transform
```

Application services depend on repository ports declared in domain. Infrastructure repositories implement those ports with Spring Data JPA.

## Shared Kernel

Shared owns cross-cutting REST errors, CORS, OpenAPI metadata, bootstrap data, exceptions, and deployment configuration.

## WebApp Integration

Nexa Platform and Nexa WebApp are deployed as separate services. The API exposes REST endpoints under `/api/v1`; the Angular WebApp calls those endpoints through its infrastructure adapters. CORS keeps local Angular origins enabled and accepts the Render frontend origin through `FRONTEND_ORIGIN`.
