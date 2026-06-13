# Database Design

Nexa Platform uses bounded-context tables generated from Spring Data JPA mappings. Local and test profiles use H2 in MySQL compatibility mode; the MySQL profile uses environment variables only.

## Profiles

| Profile | Database | Purpose |
|---|---|---|
| `local` | H2 memory, MySQL mode | Local API/browser verification |
| `test` | H2 memory, MySQL mode | Automated test suite |
| `mysql` | MySQL | Deployment or local integration with external database |

## Core Tables

| Context | Tables |
|---|---|
| IAM | `iam_users`, `iam_roles`, `iam_user_roles` |
| Catalog | `catalog_categories`, `catalog_products` |
| Warehouse | `warehouse_warehouses`, `warehouse_inventory_items`, `warehouse_stock_batches`, `warehouse_inventory_movements` |
| Sales | `sales_customers`, `sales_orders`, `sales_order_items` |
| Logistics | `logistics_delivery_routes`, `logistics_shipments`, `logistics_driver_checklists` |
| Invoicing | `invoicing_invoices`, `invoicing_invoice_lines`, `invoicing_payments` |

## Key Constraints

- IAM emails are unique.
- Catalog product `sku` is unique and exposed as `sku` plus `productCode`.
- Catalog products keep source-compatible availability fields: `available_stock`, `reserved_stock`, and `min_stock`.
- Catalog products reference `catalog_categories`.
- Inventory records reference warehouses and products.
- Sales orders reference customers and products through order lines.
- Logistics shipments reference sales orders and delivery routes.
- Invoices and payments reference sales fulfillment data.

## Seed Contract

The local/test seed supports WebApp A1 parity:

- Apps Web quick-profile users: Valeria Sanchez, Roberto Garcia, Elena Litano.
- Warehouse/operator profile for S2 coverage.
- 50 catalog products and relative image URLs copied from the source-aligned WebApp fallback catalog.
- One cold hub, one B2B customer, one route, and inventory stock for the first catalog product.

No production data or secrets are committed.
