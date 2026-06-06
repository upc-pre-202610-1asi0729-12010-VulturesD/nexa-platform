<div align="center">

<br/>

<img src="./docs/assets/nexa-logo.svg" alt="Nexa" width="200"/>

<br/><br/>

# nexa-platform

**Planned backend service layer and API foundation for the Nexa B2B platform**

<br/>

![.NET Core](https://img.shields.io/badge/.NET%20Core-8.0-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)
![C#](https://img.shields.io/badge/C%23-12-239120?style=for-the-badge&logo=c-sharp&logoColor=white)
![EF Core](https://img.shields.io/badge/EF%20Core-8.0-512BD4?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST%20API-domain-0EA5E9?style=for-the-badge)

<br/>

![Course](https://img.shields.io/badge/Course-1ASI0730%20Aplicaciones%20Web-0a2540?style=flat-square)
![Cycle](https://img.shields.io/badge/Cycle-2026--10-0a2540?style=flat-square)
![University](https://img.shields.io/badge/University-UPC-0a2540?style=flat-square)
![Team](https://img.shields.io/badge/Team-King-2a67d9?style=flat-square)
![Status](https://img.shields.io/badge/Status-Backend%20Foundation-0f766e?style=flat-square)

<br/>

🌐 **[View Live Site →](https://upc-pre-202610-1asi0730-12242-king.github.io/nexa-website/)**

<br/>

</div>

---

## Overview

The `nexa-platform` repository houses the planned ASP.NET Core backend service layer for the Nexa platform. It establishes a modular monolith foundation with bounded contexts, domain invariants, MySQL persistence structures, and REST API controllers to support catalog, order, inventory, route, and invoice workflows.

> [!NOTE]
> This repository represents a local backend foundation and architecture skeleton. Complete integration with the public website or web application is planned for future platform milestones.

---

## Nexa Repository Hub

<table>
  <tr>
    <td width="50%">
      <strong>Live Website</strong><br />
      Public landing page and product entry point.<br />
      <a href="https://upc-pre-202610-1asi0730-12242-king.github.io/nexa-website/">Open website</a>
    </td>
    <td width="50%">
      <strong>WebApp</strong><br />
      Operational frontend for product workflows.<br />
      <a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-webapp">Open repository</a>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <strong>Platform</strong><br />
      Backend/domain workspace and service foundation.<br />
      <a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform">Open repository</a>
    </td>
    <td width="50%">
      <strong>Report</strong><br />
      Academic report and project evidence.<br />
      <a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-report">Open repository</a>
    </td>
  </tr>
</table>

---

## Platform Scope

| Domain Context | Managed Aggregate | Planned API Route | Focus Area |
|---|---|---|---|
| **Sales** | `Order` | `/api/v1/orders` | Managing B2B client order validation & queues. |
| **Logistics** | `Shipment` | `/api/v1/shipments` | Delivery routes & logistics tracking. |
| **Warehouse** | `InventoryItem` | `/api/v1/inventory-items` | Real stock, reservations, location & temperature monitoring. |
| **Invoicing** | `Invoice` | `/api/v1/invoices` | Auto-generation of invoicing records. |
| **Catalog Management** | `CatalogItem` | `/api/v1/catalog-items` | Published catalog items, pricing, categories & cold-chain requirements. |
| **IAM** | `User` | `/api/v1/authentication/*` | Future authentication and access management boundary. |

---

## Repository Structure

```text
nexa-platform/
├── nexa-platform.sln           # C# Visual Studio solution file
├── docs/                       # Architecture & context specifications
│   ├── assets/                 # Branding logo files
│   ├── architecture-notes.md
│   ├── bounded-contexts.md
│   └── platform-roadmap.md
└── King.Nexa.Platform/         # Main ASP.NET Core project folder
    ├── Program.cs              # Bootstrapper entry point
    ├── appsettings.json        # Solution configs & database templates
    ├── Resources/              # Shared and context i18n resources
    ├── Migrations/             # EF migration target and MySQL init script
    ├── Shared/                 # Shared Kernel domain base
    ├── Sales/                  # Sales context domain layer
    ├── Logistics/              # Logistics context domain layer
    ├── Warehouse/              # Warehouse context domain layer
    ├── Invoicing/              # Invoicing context domain layer
    ├── CatalogManagement/      # Catalog context domain layer
    └── Iam/                    # Identity access management boundary
```

---

## Getting Started

### Local Setup
Ensure you have the compatible .NET SDK installed on your machine.

1. **Restore dependencies**:
   ```bash
   dotnet restore
   ```

2. **Build the solution**:
   ```bash
   dotnet build
   ```

3. **Start the API service**:
   ```bash
   dotnet run --project King.Nexa.Platform/King.Nexa.Platform.csproj
   ```
   *The application is configured for MySQL through Pomelo. For local development, create `nexa_platform_db` with `King.Nexa.Platform/Migrations/mysql-init.sql` and adjust `appsettings.Development.json` or copy from `appsettings.Development.example.json`.*

---

## Team

| Member | Focus | GitHub Identity |
|:---|:---|:---|
| **Diego Yucra** | Bootstrapping, Sales domain & GitFlow | [DiegoS284](https://github.com/DiegoS284) |
| **Gerard Rojas** | Shared Kernel, Persistence & Invoicing | [GerardRojasMancilla](https://github.com/GerardRojasMancilla) |
| **César Marín** | Logistics context & Domain documentation | [Cmarin2802](https://github.com/Cmarin2802) |
| **Gino Torrejón** | Catalog management context | [R0obxdnt-bit](https://github.com/R0obxdnt-bit) |
| **Joaquín Verde** | Warehouse stock verification | [JoaquinVerde115](https://github.com/JoaquinVerde115) |

---

<p align="center">
  <strong>Nexa Platform</strong> · Universidad Peruana de Ciencias Aplicadas · 2026-10
</p>
