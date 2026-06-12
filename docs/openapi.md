# OpenAPI

Swagger UI is available after startup:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI document is available at:

```text
http://localhost:8080/v3/api-docs
```

## Endpoint Coverage

| Area | Endpoint | Notes |
|---|---|---|
| IAM | `/api/v1/auth/register` | Public registration route |
| IAM | `/api/v1/auth/login` | Public login route |
| IAM | `/api/v1/users/me` | Authenticated current-user route |
| Catalog | `/api/v1/products` | Product resources include product code, brand, unit, image URL, temperature range, and status |
| Catalog | `/api/v1/categories` | Category resources |
| Warehouse | `/api/v1/inventory` | Inventory state |
| Warehouse | `/api/v1/inventory/movements` | Inventory movement records |
| Sales | `/api/v1/orders` | Sales order lifecycle |
| Sales | `/api/v1/customers` | B2B customer accounts |
| Logistics | `/api/v1/shipments` | Dispatch and tracking |
| Invoicing | `/api/v1/invoices` | Invoice lifecycle |
| Invoicing | `/api/v1/payments` | Payment records |
| Shared | `/api/v1/health` | Public health check |

## Security

Swagger and OpenAPI routes are public for academic verification. Business endpoints require JWT authentication unless explicitly configured as public auth or health routes.
