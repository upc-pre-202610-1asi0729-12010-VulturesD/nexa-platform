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
