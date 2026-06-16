<div align="center">

<br/>

<img src="./docs/assets/nexa-logo.svg" alt="Nexa" width="200"/>

<br/><br/>

# nexa-platform

**Backend platform and API service layer for the Nexa B2B cold-chain distribution platform**

<br/>

![.NET Core](https://img.shields.io/badge/.NET%20Core-10.0-512BD4?style=for-the-badge&logo=dotnet&logoColor=white)
![C#](https://img.shields.io/badge/C%23-14-239120?style=for-the-badge&logo=c-sharp&logoColor=white)
![EF Core](https://img.shields.io/badge/EF%20Core-9.0-512BD4?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![REST API](https://img.shields.io/badge/REST%20API-domain-0EA5E9?style=for-the-badge)

<br/>

![Course](https://img.shields.io/badge/Course-1ASI0730%20Aplicaciones%20Web-0a2540?style=flat-square)
![Cycle](https://img.shields.io/badge/Cycle-2026--10-0a2540?style=flat-square)
![University](https://img.shields.io/badge/University-UPC-0a2540?style=flat-square)
![Team](https://img.shields.io/badge/Team-King-2a67d9?style=flat-square)
![Status](https://img.shields.io/badge/Status-Release%201.0.0-22c55e?style=flat-square)

<br/>

🌐 **[View Live Site →](https://upc-pre-202610-1asi0730-12242-king.github.io/nexa-website/)**

<br/>

</div>

---

## Overview

The `nexa-platform` repository houses the ASP.NET Core backend service layer for the Nexa platform. Built using Clean Architecture and Domain-Driven Design (DDD) principles, it exposes modular REST APIs organized across multiple bounded contexts to manage catalog, inventory, order, logistics routing, and invoice workflows.

---

## Repository Map

<table>
  <tr>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-website">nexa-website</a></p>
      <p>Public landing website and central product entry point.</p>
      <p><a href="https://upc-pre-202610-1asi0730-12242-king.github.io/nexa-website/">Open Live Website</a></p>
      <p>
        <img alt="HTML5" src="https://img.shields.io/badge/HTML5-static-E34F26?style=flat-square&logo=html5&logoColor=white" />
        <img alt="CSS3" src="https://img.shields.io/badge/CSS3-responsive-1572B6?style=flat-square&logo=css3&logoColor=white" />
        <img alt="JavaScript" src="https://img.shields.io/badge/JavaScript-vanilla-F7DF1E?style=flat-square&logo=javascript&logoColor=black" />
      </p>
    </td>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-webapp">nexa-webapp</a></p>
      <p>Main web application for operational workflows and buyer-facing coordination.</p>
      <p><a href="https://nexa-webapp.onrender.com/#/auth/login">Open Live WebApp Login</a></p>
      <p>
        <img alt="Vue 3" src="https://img.shields.io/badge/Vue%203-35495E?style=flat-square&logo=vue.js&logoColor=4FC08D" />
        <img alt="Vite" src="https://img.shields.io/badge/Vite-0F172A?style=flat-square&logo=vite&logoColor=FFD62E" />
        <img alt="PrimeVue" src="https://img.shields.io/badge/PrimeVue-0EA5E9?style=flat-square" />
      </p>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform">nexa-platform</a> (This Repository)</p>
      <p>Platform and backend work area for API, domain, and infrastructure concerns.</p>
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform/wiki">Open Engineering Wiki</a></p>
      <p>
        <img alt="Platform" src="https://img.shields.io/badge/Platform-backend%20workspace-512BD4?style=flat-square" />
        <img alt="API" src="https://img.shields.io/badge/API-domain%20services-0EA5E9?style=flat-square" />
        <img alt="EF Core" src="https://img.shields.io/badge/EF%20Core-9.0-512BD4?style=flat-square" />
      </p>
    </td>
    <td width="50%">
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-ecosystem-report">nexa-ecosystem-report</a></p>
      <p>Academic report, product research, backlog, architecture documentation, and project evidence.</p>
      <p><a href="https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-ecosystem-report">Open Report Repository</a></p>
      <p>
        <img alt="Documentation" src="https://img.shields.io/badge/Documentation-report-0F172A?style=flat-square" />
        <img alt="UPC" src="https://img.shields.io/badge/UPC-course%20evidence-0EA5E9?style=flat-square" />
      </p>
    </td>
  </tr>
</table>

---

## Application Areas

| Area | Backend Responsibility | Key Aggregates |
|---|---|---|
| **Sales** | Order creation, validation, pricing calculations, and confirmation status. | `Order` |
| **Warehouse** | Real stock levels, reserved stock, temperature monitoring, and warehouse locations. | `InventoryItem`, `Warehouse` |
| **Logistics** | Dispatch planning, logistics scheduling, delivery status updates, and route tracking. | `Shipment` |
| **Invoicing** | Invoices billing, payments registration, billing reference tracking, and payment matching. | `Invoice`, `Payment` |
| **Catalog Management** | Product descriptions, units price, stock quantity, brands, categories, and cold-chain requirements. | `CatalogItem`, `Brand`, `Category` |
| **IAM** | User signup, sign-in, password security, session token generation. | `User` |

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Core Framework** | .NET Core 10.0 |
| **Language** | C# 14 |
| **Object-Relational Mapper** | Entity Framework Core 9.0 |
| **Database** | PostgreSQL 16 |
| **API Tooling & Docs** | Swagger / OpenAPI (Swashbuckle) |
| **Architecture** | Clean Architecture & Domain-Driven Design (DDD) |

---

## Getting Started

### Local Setup

1.  **Prerequisites**:
    - Install the [.NET 10 SDK](https://dotnet.microsoft.com/download/dotnet/10.0).
    - Install the [EF Core CLI tool](https://learn.microsoft.com/en-us/ef/core/cli/dotnet):
      ```bash
      dotnet tool install --global dotnet-ef
      ```
    - Ensure you have a local **PostgreSQL** instance running.

2.  **Configuration**:
    - Navigate to the project root directory.
    - Create a local configuration file called `appsettings.Local.json` by copying the template:
      ```bash
      cp King.Nexa.Platform/appsettings.Local.example.json King.Nexa.Platform/appsettings.Local.json
      ```
    - Update `appsettings.Local.json` with your local PostgreSQL server connection details:
      ```json
      {
        "ConnectionStrings": {
          "DefaultConnection": "Host=localhost;Port=5432;Database=nexa_platform_db;Username=postgres;Password=your_password;SSL Mode=Prefer;Trust Server Certificate=true"
        }
      }
      ```
      *(Note: `appsettings.Local.json` is git-ignored to prevent database credential leaks).*

3.  **Run Migrations**:
    Apply the database migrations to create the database schema:
    ```bash
    dotnet ef database update --project King.Nexa.Platform
    ```

4.  **Run the Server**:
    Start the backend application:
    ```bash
    dotnet run --project King.Nexa.Platform
    ```
    *The API will start listening locally at `http://localhost:5068/` and the Swagger UI will be accessible at `http://localhost:5068/swagger`.*

---

## Available Commands

| Command | Action |
|---|---|
| `dotnet restore` | Restores NuGet dependencies. |
| `dotnet build` | Compiles the backend project. |
| `dotnet run --project King.Nexa.Platform` | Starts the local API server. |
| `dotnet ef database update --project King.Nexa.Platform` | Applies migrations to the database. |
| `dotnet ef migrations add <Name> --project King.Nexa.Platform` | Creates a new EF migration. |

---

## Project Structure

```text
King.Nexa.Platform/
├── CatalogManagement/   # Product catalog, categories, and brands boundary
├── Iam/                 # Authentication, users, password hashing, and token boundary
├── Invoicing/           # Invoices billing, payments registration boundary
├── Logistics/           # Delivery route coordination and shipment boundary
├── Sales/               # Commercial orders registration and validation boundary
├── Shared/              # Shared kernel (generic bases, Unit of Work, exception handling)
├── Warehouse/           # Inventory stock tracking, warehouses, reservations boundary
├── Migrations/          # Entity Framework database schema migrations
├── Resources/           # Localized XML resources (.resx)
└── Program.cs           # Main entry point and MVC pipeline configuration
```

---

## Team & Domain Ownership

To keep development organized, specific contexts are assigned to primary owners:

| Context | Owner | Support |
|---|---|---|
| **Sales** | DiegoS284 | Cmarin2802, R0obxdnt-bit |
| **Logistics** | Cmarin2802 | DiegoS284, GerardRojasMancilla |
| **Warehouse** | JoaquinVerde115 | R0obxdnt-bit, DiegoS284 |
| **Invoicing** | GerardRojasMancilla | Cmarin2802, DiegoS284 |
| **Catalog Management** | R0obxdnt-bit | JoaquinVerde115, DiegoS284 |

---

## Documentation

Full backend specifications, database guides, and developer workflows are maintained in:
- **[Nexa Platform Wiki Index](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform/wiki)**
- [Backend Clean Architecture Guidelines](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform/wiki/Backend-Architecture)
- [REST API Design Guidelines](https://github.com/upc-pre-202610-1asi0730-12242-king/nexa-platform/wiki/API-Design-Guidelines)
- [Security Policy (SECURITY.md)](SECURITY.md)

---

<p align="center">
  <strong>Nexa Platform</strong> · Universidad Peruana de Ciencias Aplicadas · 2026-10
</p>
