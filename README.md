<div align="center">

<br/>

<img src="./docs/assets/nexa-logo.svg" alt="Nexa" width="220"/>

<br/><br/>

# nexa-platform

**Java Spring Boot RESTful API for Nexa B2B cold-chain operations**

<br/>

![Java](https://img.shields.io/badge/Java-21-0a2540?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Render-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-local%20%7C%20test-0EA5E9?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST%20API-%2Fapi%2Fv1-0EA5E9?style=for-the-badge)

<br/>

![Course](https://img.shields.io/badge/Course-1ASI0729%20Desarrollo%20de%20Aplicaciones%20Open%20Source-0a2540?style=flat-square)
![Cycle](https://img.shields.io/badge/Cycle-2026--10-0a2540?style=flat-square)
![University](https://img.shields.io/badge/University-UPC-0a2540?style=flat-square)
![Team](https://img.shields.io/badge/Team-Nexa-2a67d9?style=flat-square)
![Status](https://img.shields.io/badge/Status-Release%201.0.1-22c55e?style=flat-square)

<br/>

🔗 **[Open Engineering Wiki →](wiki/Home.md)**

<br/>

</div>

---

## Overview

`nexa-platform` is the backend/platform repository for Nexa, a B2B cold-chain operations system for importer-distributors, commercial teams, logistics operators, warehouse users, and B2B buyers.

The API is implemented with Java 21 and Spring Boot. It exposes RESTful endpoints under `/api/v1`, uses H2 for local/test verification, and is prepared for PostgreSQL on Render through `render.yaml`.

---

## Repository Map

<table>
  <tr>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-website">upc-pre-202610-1asi0729-12010-VulturesD/nexa-website</a></p>
      <p>Public landing website and central product entry point.</p>
      <p><a href="https://upc-pre-202610-1asi0729-12010-vulturesd.github.io/nexa-website/">Open Live Website</a></p>
      <p>
        <img alt="HTML5" src="https://img.shields.io/badge/HTML5-static-E34F26?style=flat-square&logo=html5&logoColor=white" />
        <img alt="CSS3" src="https://img.shields.io/badge/CSS3-responsive-1572B6?style=flat-square&logo=css3&logoColor=white" />
        <img alt="JavaScript" src="https://img.shields.io/badge/JavaScript-vanilla-F7DF1E?style=flat-square&logo=javascript&logoColor=black" />
      </p>
    </td>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-webapp">upc-pre-202610-1asi0729-12010-VulturesD/nexa-webapp</a></p>
      <p>Angular Web Application for operational workflows and buyer-facing coordination.</p>
      <p><a href="https://nexa-webapp-fv2v.onrender.com/login">Open Live WebApp Login</a></p>
      <p>
        <img alt="Angular" src="https://img.shields.io/badge/Angular-21-DD0031?style=flat-square&logo=angular&logoColor=white" />
        <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5.9-3178C6?style=flat-square&logo=typescript&logoColor=white" />
        <img alt="Angular Material" src="https://img.shields.io/badge/Angular%20Material-21-0a2540?style=flat-square" />
      </p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-platform">upc-pre-202610-1asi0729-12010-VulturesD/nexa-platform</a> (This Repository)</p>
      <p>Spring Boot backend platform and REST API service layer.</p>
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-platform/wiki">Open Engineering Wiki</a></p>
      <p>
        <img alt="Java" src="https://img.shields.io/badge/Java-21-0a2540?style=flat-square" />
        <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white" />
        <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-Render-4169E1?style=flat-square&logo=postgresql&logoColor=white" />
      </p>
    </td>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-report">upc-pre-202610-1asi0729-12010-VulturesD/nexa-report</a></p>
      <p>Academic report, product research, backlog, architecture documentation, and project evidence.</p>
      <p><a href="https://github.com/upc-pre-202610-1asi0729-12010-VulturesD/nexa-report">Open Report Repository</a></p>
      <p>
        <img alt="Documentation" src="https://img.shields.io/badge/Documentation-report-0F172A?style=flat-square" />
        <img alt="Open Source" src="https://img.shields.io/badge/Open%20Source-course%20evidence-0EA5E9?style=flat-square" />
      </p>
    </td>
  </tr>
</table>

---

## Bounded Contexts

| Context | Responsibility |
| --- | --- |
| IAM | Authentication, authorization, JWT sessions, user profiles |
| Catalog Management | Products, categories, cold-chain requirements, catalog assets |
| Sales | B2B customers, purchase requests, sales orders, order status |
| Warehouse | Warehouses, inventory, lots, stock movements, reorder alerts |
| Logistics | Shipments, delivery routes, tracking, driver checklists |
| Invoicing | Invoices, invoice lines, payments, business documents |
| Promotions | Commercial promotions for buyer and operations workflows |
| Shared | CORS, OpenAPI, errors, health, deployment config, bootstrap data |

Each context keeps domain, application, infrastructure, and interfaces layers. Application services depend on domain repository ports; Spring Data JPA adapters live in infrastructure.

---

## Tech Stack

| Layer | Technology |
| --- | --- |
| Runtime | Java 21 |
| Framework | Spring Boot 3.3 |
| API | Spring Web, Bean Validation, Springdoc OpenAPI |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Local/Test DB | H2 |
| Production DB | PostgreSQL on Render |
| Deployment | Docker + Render Blueprint |

---

## Runtime Profiles

| Profile | Database | Use |
| --- | --- | --- |
| `local` | H2 in memory | Local verification |
| `test` | H2 in memory | Automated tests |
| `postgres` | PostgreSQL | Render or PostgreSQL-compatible runtime |
| `seed` | Active datasource | Academic demo data for review environments |

Render uses `SPRING_PROFILES_ACTIVE=postgres,seed`.

---

## Local Setup

```bash
./mvnw test
./mvnw package
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Local API:

```text
http://localhost:8080/api/v1
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Health check:

```text
http://localhost:8080/actuator/health
```

---

## Render Deployment

`render.yaml` defines:

- Docker web service `nexa-platform`.
- Managed PostgreSQL database.
- `DATABASE_URL` mapped from Render.
- `SPRING_PROFILES_ACTIVE=postgres,seed`.
- `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`, `JPA_DDL_AUTO`.
- `FRONTEND_ORIGIN` for CORS with the deployed WebApp.
- Health check at `/actuator/health`.

No database credentials are committed.

---

## Demo Users

| Role | Email | Password |
| --- | --- | --- |
| Commercial coordinator | `sales@nexa.com` | `NexaAccess2026!` |
| Logistics lead | `logistics@nexa.com` | `NexaAccess2026!` |
| Warehouse operator | `warehouse@nexa.com` | `NexaAccess2026!` |
| Buyer | `buyer@nexa.com` | `NexaAccess2026!` |

The `seed` profile also loads catalog products, categories, customers, orders, inventory, invoices, payments, delivery routes, and promotions.

---

## Team & Domain Ownership

To keep development organized, bounded contexts are assigned to primary owners and support contributors:

| Context | Owner | Support |
| --- | --- | --- |
| **Sales** | DiegoS284 | Cmarin2802, R0obxdnt-bit |
| **Logistics** | Cmarin2802 | DiegoS284, GerardRojasMancilla |
| **Warehouse** | JoaquinVerde115 | R0obxdnt-bit, DiegoS284 |
| **Invoicing** | GerardRojasMancilla | Cmarin2802, DiegoS284 |
| **Catalog Management** | R0obxdnt-bit | JoaquinVerde115, DiegoS284 |

---

## Documentation

- [Architecture](docs/architecture.md)
- [Deployment](docs/deployment.md)
- [Database](docs/database.md)
- [Database seed data](docs/database-seed-data.md)
- [OpenAPI notes](docs/openapi.md)
- [Release notes](docs/releases/README.md)
- [Contributing](.github/CONTRIBUTING.md)
- [Security policy](.github/SECURITY.md)

---

<p align="center">
  <strong>Nexa Platform</strong> · Team Nexa · Universidad Peruana de Ciencias Aplicadas · 2026-10
</p>
