<div align="center">

<img src="https://raw.githubusercontent.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-website/main/nexa.svg" alt="Nexa" width="200"/>

# nexa-platform

**Java Spring Boot API for Nexa B2B cold-chain operations**

![Java](https://img.shields.io/badge/Java-21-0a2540?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6db33f?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

</div>

## Purpose

`nexa-platform` exposes RESTful services for a B2B cold-chain distribution workflow: catalog management, inventory control, sales orders, dispatch tracking, invoicing, and IAM.

## Architecture

The platform follows bounded contexts with layered packages:

| Context | Responsibility |
|---|---|
| IAM | Authentication, authorization, and current user access |
| Catalog Management | Products, categories, suppliers, and cold-chain requirements |
| Warehouse | Warehouses, inventory, batches, movements, and alerts |
| Sales | Customers, sales orders, order items, and status changes |
| Logistics | Shipments, routes, driver checklists, and tracking |
| Invoicing | Invoices, invoice lines, payments, and payment status |
| Shared | Configuration, errors, OpenAPI, CORS, and seed data |

Each context keeps `domain`, `application`, `infrastructure`, and `interfaces` layers.

## Runtime profiles

| Profile | Database | Use case |
|---|---|---|
| `mysql` | MySQL | Development or deployment with environment variables |
| `local` | H2 in memory | Local API verification without external services |
| `test` | H2 in memory | Automated tests |

Required variables for the `mysql` profile:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=nexa_platform_db
DB_USERNAME=nexa_user
DB_PASSWORD=change-me
JWT_SECRET=change-this-secret-with-at-least-32-characters
JWT_EXPIRATION_MINUTES=120
```

## Run locally

```bash
./mvnw test
./mvnw package
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Main endpoints

| Area | Endpoints |
|---|---|
| IAM | `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `GET /api/v1/users/me` |
| Catalog | `GET /api/v1/products`, `POST /api/v1/products`, `GET /api/v1/categories` |
| Warehouse | `GET /api/v1/warehouses`, `GET /api/v1/inventory`, `POST /api/v1/inventory/movements`, `GET /api/v1/inventory/alerts` |
| Sales | `GET /api/v1/customers`, `POST /api/v1/orders`, `GET /api/v1/orders`, `PATCH /api/v1/orders/{id}/status` |
| Logistics | `POST /api/v1/shipments`, `GET /api/v1/shipments`, `GET /api/v1/shipments/{id}/tracking`, `POST /api/v1/driver-checklists` |
| Invoicing | `POST /api/v1/invoices`, `GET /api/v1/invoices`, `POST /api/v1/payments` |

## Branching

The repository uses GitFlow:

- `main` for stable releases.
- `develop` for integration.
- `feature/*` for bounded-context work.
- `release/*` for release stabilization.

## Team

| GitHub | Responsibility |
|---|---|
| DiegoS284 | Shared Kernel, IAM, Sales |
| Cmarin2802 | Logistics |
| JoaquinVerde115 | Warehouse and testing |
| GerardRojasMancilla | Invoicing and OpenAPI |
| R0obxdnt-bit | Catalog Management |
