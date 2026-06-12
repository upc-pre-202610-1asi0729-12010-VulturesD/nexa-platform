# Platform Database Gap Map

| Bounded Context | Table/Entity | PK | FK/Relations | Constraints | Seed Required | Status | Action |
|---|---|---|---|---|---|---|---|
| IAM | `iam_users`, `iam_roles`, `iam_user_roles` | Identity IDs | User-role join table | Email uniqueness and role-name enum implied | Admin/operator/buyer users | Partial | Add buyer/commercial/logistics seed users aligned to WebApp demo profiles. |
| Catalog Management | `catalog_categories`, `catalog_products` | Identity IDs | Product belongs to category | Unique product SKU index | Full approved catalog categories/products | Partial | Expand product seed and add media fields for product images. |
| Catalog Management | `ColdChainRequirement` embedded fields | Product row | Embedded in product | Temperature range and handling text | Required for all cold-chain products | Partial | Normalize temperature bounds and handling notes from source product metadata. |
| Warehouse | `warehouse_locations` | Identity ID | Inventory items reference warehouse/product | Name/location fields; temperature band enum | Cold hub locations | Partial | Add source-compatible warehouses and temperature-band inventory coverage. |
| Warehouse | `warehouse_inventory_items` | Identity ID | Warehouse and product references | Reorder quantity fields | Inventory per product/warehouse | Partial | Seed inventory for every catalog item required by WebApp fallback. |
| Warehouse | `warehouse_stock_batches` | Identity ID | Inventory item reference | Lot code and expiration data | Lot data | Partial | Add lot records needed for inventory/lots screen parity. |
| Warehouse | `warehouse_inventory_movements` | Identity ID | Inventory item reference | Movement type enum | Movement history | Partial | Seed stock movements aligned with dashboard and inventory audit. |
| Sales | `sales_customers` | Identity ID | Orders reference customer | Tax/account identifiers implied | B2B customers, contacts, addresses | Partial | Add contacts/address support or linked tables if needed by WebApp client parity. |
| Sales | `sales_orders`, `sales_order_items` | Identity IDs | Order items reference order/product | Order status enum and totals | Purchase requests/orders | Partial | Add request workflow or status mapping matching source commercial and buyer flows. |
| Logistics | `logistics_delivery_routes` | Identity ID | Shipments reference route/customer/order where modeled | Route name/origin/destination | Route plans | Partial | Add route seed and richer dispatch status data. |
| Logistics | `logistics_shipments`, `logistics_driver_checklists` | Identity IDs | Shipment/checklist relation | Shipment status enum | Dispatch, proof, checklist data | Partial | Add proof/evidence and customer portal fields if kept in logistics. |
| Invoicing | `invoicing_invoices`, `invoicing_invoice_lines`, `invoicing_payments` | Identity IDs | Lines/payments reference invoice | Invoice status enum and payment data | Buyer payment/credit records | Partial | Align invoices and payments with buyer portal payment-method screens. |
| Buyer Portal / Segment 3 | No dedicated tables | Not present | Uses Sales/Catalog/Invoicing/Logistics data | Not present | Portal requirements, uploads, support, premium preview | Missing | Add either dedicated portal tables or source-compatible read models. |

## Configuration Findings

- MySQL profile exists in `src/main/resources/application-mysql.yml`.
- Local H2 profile exists in `src/main/resources/application-local.yml`.
- Default profile uses local H2 for verification.
- JPA `ddl-auto` is `update` for MySQL and `create-drop` for local H2.
- Explicit physical naming strategy was not found.
- OpenAPI configuration exists.
- Seed data is restricted to local/test profiles.

## Critical Database Gaps

- Catalog product table lacks image/media reference fields.
- Seed data does not cover full catalog, buyer portal, payments, support, documents, or timeline evidence.
- Domain models are persistence entities, so database restructuring must be careful and incremental.
- Buyer Portal / Segment 3 has no clear platform persistence ownership yet.
- Client contacts and delivery addresses from the source dataset are not represented as separate relational tables.

## Required Database Order

1. Add catalog image/media fields and seed expanded catalog data.
2. Add source-compatible customer contacts and delivery addresses.
3. Add buyer request/order detail data needed by Segment 3 pages.
4. Add logistics proof/evidence seed coverage.
5. Add payment/document/support records or read models for buyer portal parity.
6. Run H2 tests first, then MySQL profile validation if credentials are available.
