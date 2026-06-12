# Database Schema Checklist

| Context | Table | Purpose | Relationship Notes | Status |
|---|---|---|---|---|
| IAM | `iam_users`, `iam_roles`, `iam_user_roles` | Authentication and role assignment | User-role join table | Ready |
| Catalog | `catalog_categories`, `catalog_products` | Product reference data and media URL contract | Product belongs to category | Ready |
| Warehouse | `warehouse_warehouses`, `warehouse_inventory_items`, `warehouse_stock_batches`, `warehouse_inventory_movements` | Stock state, lots, and movement history | Inventory links warehouse and product | Ready for demo, lot coverage partial |
| Sales | `sales_customers`, `sales_orders`, `sales_order_items` | Customer and order lifecycle | Orders link customers and products | Ready for demo, request-depth partial |
| Logistics | `logistics_delivery_routes`, `logistics_shipments`, `logistics_driver_checklists` | Dispatch planning, tracking, and proof flow | Shipments link route and order | Ready for demo, proof media partial |
| Invoicing | `invoicing_invoices`, `invoicing_invoice_lines`, `invoicing_payments` | Billing and payment tracking | Payments link invoice | Ready for demo |

## Naming

- Explicit table names use bounded-context prefixes.
- Shared naming strategy converts generated names to snake_case.
- MySQL and H2 profiles use the same naming strategy.

## Additive Schema Changes In This Alignment

- `catalog_products.unit`
- `catalog_products.image_url`

Both fields are additive. `unit` is nullable at database level so existing MySQL rows can migrate without destructive changes; domain serialization defaults missing values to `box`.
