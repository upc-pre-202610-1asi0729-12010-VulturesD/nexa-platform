# Nexa Platform Architecture

The API is organized around bounded contexts. Each context owns its domain model, application services, infrastructure adapters, and REST interfaces. Shared code is restricted to cross-cutting concerns such as security configuration, OpenAPI metadata, error handling, CORS, and seed data.

## Context map

- IAM authenticates operators and exposes the current user profile.
- Catalog Management owns product and category reference data.
- Warehouse owns stock state, warehouses, inventory movements, and alerts.
- Sales owns customers and order lifecycle.
- Logistics owns shipments, delivery routes, tracking, and driver checklist data.
- Invoicing owns invoices and payments.

## Persistence

The `mysql` profile uses environment variables and Spring Data JPA mappings. The `local` and `test` profiles use H2 with MySQL compatibility mode for quick verification.

## Professor-Style Layers

The platform follows the course reference through visible bounded-context layers:

```text
com.nexa.platform.<context>
  domain/model
  application/commands
  application/queries
  application/commandservices
  application/queryservices
  application/internal
  infrastructure/persistence/jpa
  interfaces/rest/resources
  interfaces/rest/transform
```

Catalog now applies the full command/query/resource/transform pattern. Other bounded contexts keep the existing layered structure and can be migrated incrementally without changing public behavior.

## Shared Kernel

Shared owns cross-cutting REST errors, CORS, OpenAPI metadata, bootstrap seed data, exceptions, and persistence naming strategy.
