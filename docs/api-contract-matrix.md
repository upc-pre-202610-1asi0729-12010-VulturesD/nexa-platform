# API Contract Matrix

| Context | Endpoint Base | Methods |
| --- | --- | --- |
| IAM | `/api/v1/auth`, `/api/v1/users` | `GET`, `POST` |
| Catalog | `/api/v1/products`, `/api/v1/catalog-items`, `/api/v1/categories` | `GET`, `POST`, `PUT`, `DELETE` |
| Sales | `/api/v1/customers`, `/api/v1/orders`, `/api/v1/purchase-requests` | `GET`, `POST`, `PATCH` |
| Warehouse | `/api/v1/warehouses`, `/api/v1/inventory` | `GET`, `POST` |
| Logistics | `/api/v1/shipments`, `/api/v1/driver-checklists` | `GET`, `POST` |
| Invoicing | `/api/v1/invoices`, `/api/v1/payments`, `/api/v1/business-documents` | `GET`, `POST` |
| Promotions | `/api/v1/promotions` | `GET`, `POST` |

All REST responses are exposed through resource classes at the interfaces layer.
