<div align="center">

<br/>

# Nexa Platform

**Planned backend service layer for the Nexa B2B platform — current foundation milestone.**

<br/>

![Course](https://img.shields.io/badge/Course-1ASI0730%20Aplicaciones%20Web-0a2540?style=flat-square)
![Cycle](https://img.shields.io/badge/Cycle-2026--10-0a2540?style=flat-square)
![University](https://img.shields.io/badge/University-UPC-0a2540?style=flat-square)
![Team](https://img.shields.io/badge/Team-King-2a67d9?style=flat-square)
![Status](https://img.shields.io/badge/Status-Backend%20Foundation-0f766e?style=flat-square)
![Version](https://img.shields.io/badge/Version-v0.1.0-111827?style=flat-square)

<br/>

</div>

---

## Overview

`nexa-platform` is the early ASP.NET Core foundation for the future Nexa backend service layer. It defines the initial project structure, shared kernel, persistence base, and bounded context folders that will support later platform milestones.

This repository is not a production deployment and does not claim completed backend integration with the public website or web application.

## Current Status

| Item | Status |
|---|---|
| Latest version | `v0.1.0` |
| Platform phase | Active backend foundation |
| Backend production readiness | Not claimed |
| Frontend integration | Future milestone |
| Deployment | Future milestone |

## Tech Stack

- ASP.NET Core
- C#
- Entity Framework Core
- REST API
- Swagger/OpenAPI-ready project structure
- DDD-inspired bounded context organization

## Bounded Contexts

| Bounded Context | Aggregate | Base route | Owner |
|---|---|---|---|
| Sales | `Order` | `/api/v1/orders` | DiegoS284 |
| Logistics | `Shipment` | `/api/v1/shipments` | Cmarin2802 |
| Warehouse | `InventoryItem` | `/api/v1/inventory-items` | JoaquinVerde115 |
| Invoicing | `Invoice` | `/api/v1/invoices` | GerardRojasMancilla |
| Catalog Management | `Product` | `/api/v1/products` | R0obxdnt-bit |

## Repository Structure

```txt
nexa-platform/
├── .gitignore
├── README.md
├── nexa-platform.sln
├── docs/
│   ├── architecture-notes.md
│   ├── bounded-contexts.md
│   └── platform-roadmap.md
└── King.Nexa.Platform/
    ├── King.Nexa.Platform.csproj
    ├── Program.cs
    ├── appsettings.json
    ├── appsettings.Development.json
    ├── Properties/
    ├── Shared/
    ├── Sales/
    ├── Logistics/
    ├── Warehouse/
    ├── Invoicing/
    └── CatalogManagement/
```

## Local Setup

Install a compatible .NET SDK for the target framework used by the project.

```bash
dotnet restore
dotnet build
dotnet run --project King.Nexa.Platform/King.Nexa.Platform.csproj
```

The default configuration uses a local placeholder connection string named `DefaultConnection`. Replace it through local configuration or environment-specific settings before running against a real database.

## GitFlow Strategy

| Branch type | Purpose |
|---|---|
| `main` | Stable release history |
| `develop` | Integration branch for completed platform work |
| `feature/*` | Bounded context or platform foundation work |
| `release/*` | Release candidate stabilization |
| `hotfix/*` | Critical release fixes, only when needed |

Temporary feature and release branches are merged into `develop` with merge commits and deleted after completion.

## Documentation

- [Architecture notes](docs/architecture-notes.md)
- [Bounded contexts](docs/bounded-contexts.md)
- [Platform roadmap](docs/platform-roadmap.md)

## Team

| GitHub | Email | Area |
|---|---|---|
| DiegoS284 | diego64g284@gmail.com | API bootstrapping, Sales support, GitFlow, README |
| GerardRojasMancilla | u202413142@upc.edu.pe | Shared kernel, persistence base, Invoicing |
| Cmarin2802 | cesarmarin2802@gmail.com | Logistics, context documentation |
| R0obxdnt-bit | u202416289@upc.edu.pe | Catalog Management, Sales support |
| JoaquinVerde115 | u20241a054@upc.edu.pe | Warehouse, setup review |

## Related Repositories

| Repository | Description |
|---|---|
| [nexa-website](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-website) | Public website |
| [nexa-webapp](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-webapp) | Frontend web application |
| [nexa-report](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-report) | Academic report |

---

<div align="center">

**Nexa** · Universidad Peruana de Ciencias Aplicadas · 2026-10

*1ASI0730 — Aplicaciones Web · Ingeniería de Software*

</div>
