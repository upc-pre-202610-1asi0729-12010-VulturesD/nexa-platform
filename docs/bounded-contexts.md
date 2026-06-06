# Bounded Contexts

The v0.2.0 local platform foundation defines five business bounded contexts, one IAM application context, and a Shared Kernel. The first core endpoints are Catalog Management, Sales, and Warehouse.

| Context | Owner | Aggregate | REST resource |
|---|---|---|---|
| Sales | DiegoS284 | `Order` | `/api/v1/orders` |
| Logistics | Cmarin2802 | `Shipment` | `/api/v1/shipments` |
| Warehouse | JoaquinVerde115 | `InventoryItem` | `/api/v1/inventory-items` |
| Invoicing | GerardRojasMancilla | `Invoice` | `/api/v1/invoices` |
| Catalog Management | R0obxdnt-bit | `CatalogItem` | `/api/v1/catalog-items` |
| IAM | Shared | `User` | `/api/v1/authentication/*` |

## Shared ownership

| Area | Owner | Helpers |
|---|---|---|
| Shared kernel | GerardRojasMancilla | DiegoS284 |
| Persistence base | GerardRojasMancilla | Cmarin2802 |
| API bootstrapping | DiegoS284 | GerardRojasMancilla |
| README and documentation | DiegoS284 | Cmarin2802 |
| GitFlow and release hygiene | DiegoS284 | GerardRojasMancilla |

## Scope boundary

This foundation is designed for future platform work. It does not claim completed authentication, production deployment, full persistence migrations, or integration with the current frontend applications.
