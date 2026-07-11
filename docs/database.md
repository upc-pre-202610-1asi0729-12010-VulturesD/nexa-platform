# Database

Nexa Platform uses PostgreSQL for deployment and H2 for automated tests.

## PostgreSQL Profile

Use the `postgres` profile for Render or any PostgreSQL-compatible deployment:

```bash
SPRING_PROFILES_ACTIVE=postgres,seed
DATABASE_URL=postgresql://user:password@host:5432/database
```

The Render Blueprint in `render.yaml` provides the database connection string through environment variables. Production credentials are not committed.

## Persistence

The schema is generated from Spring Data JPA mappings during academic verification. Bounded contexts own their tables and relationships:

- IAM: users, roles, user-role assignments.
- Catalog: categories, products, cold-chain requirements.
- Sales: customers, orders, order items.
- Warehouse: warehouses, stock, lots, movements.
- Logistics: routes, shipments, driver checklists.
- Invoicing: invoices, invoice lines, payments.
- Promotions: commercial promotion records.

## Seed Data

The local/test profiles and the explicit `seed` profile create workspace users and business records for validating the WebApp and RESTful API:

- `sales@nexa.com`
- `logistics@nexa.com`
- `warehouse@nexa.com`
- `buyer@nexa.com`

Password for workspace validation profiles:

```text
NexaAccess2026!
```
