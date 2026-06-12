# AV2 Feature and Story Validation

Story and epic codes must come from Jira/report evidence. If exact IDs are not present in this repository, keep `TBD from Jira/report`.

| Feature | Bounded Context | Platform Endpoint(s) | Expected HTTP Status | Story/Epic Code | Evidence |
|---|---|---|---|---|---|
| Product Catalog | Catalog Management | `/api/v1/catalog-items`, `/api/v1/categories`, `/api/v1/brands` | 200/201 | TBD from Jira/report | Swagger + Workbench |
| Inventory Control | Warehouse | `/api/v1/warehouses`, `/api/v1/inventory-items` | 200/201 | TBD from Jira/report | Swagger + Workbench |
| Purchase Orders | Sales | `/api/v1/orders` | 200/201 | TBD from Jira/report | Swagger |
| Dispatch Orders | Logistics | `/api/v1/shipments` | 200/201 | TBD from Jira/report | Swagger |
| Business Documents | Invoicing | `/api/v1/invoices`, `/api/v1/payments` | 200/201 | TBD from Jira/report | Swagger |

## Endpoint Validation Targets

```bash
curl -i http://localhost:5068/swagger
curl -i http://localhost:5068/api/v1/categories
curl -i http://localhost:5068/api/v1/brands
curl -i http://localhost:5068/api/v1/catalog-items
curl -i http://localhost:5068/api/v1/warehouses
curl -i http://localhost:5068/api/v1/inventory-items
curl -i http://localhost:5068/api/v1/orders
curl -i http://localhost:5068/api/v1/shipments
curl -i http://localhost:5068/api/v1/invoices
curl -i http://localhost:5068/api/v1/payments
```

Main GET endpoints should return 200 after MySQL database creation, migrations, and local seed setup.
