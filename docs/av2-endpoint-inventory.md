# Nexa AV2 Endpoint Inventory And Backend Expansion Plan

Scope: inventory and plan only. No endpoints implemented in this pass.

Current routing note: controllers using `[Route("api/v1/[controller]")]` are transformed by `KebabCaseRouteNamingConvention`, so `CatalogItemsController` exposes `api/v1/catalog-items`, `InventoryItemsController` exposes `api/v1/inventory-items`, and so on.

## Current Endpoint Count

| Controller | Bounded context | Count |
|---|---:|---:|
| AuthenticationController | Iam | 2 |
| CatalogItemsController | CatalogManagement | 3 |
| InventoryItemsController | Warehouse | 6 |
| OrdersController | Sales | 6 |
| ShipmentsController | Logistics | 4 |
| InvoicesController | Invoicing | 4 |
| Total | All | 25 |

Count includes `[HttpGet]`, `[HttpPost]`, `[HttpPut]`, `[HttpDelete]`, and `[HttpPatch]`.

## Current Endpoints

| Bounded context | Controller | Method | Route | Current command/query/service used | Current resource/assembler used |
|---|---|---:|---|---|---|
| Iam | AuthenticationController | POST | `/api/v1/authentication/sign-up` | `SignUpCommand`; `IUserCommandService.SignUpAsync` | `SignUpResource`; `SignUpCommandFromResourceAssembler`; `AuthenticatedUserResourceFromEntityAssembler` |
| Iam | AuthenticationController | POST | `/api/v1/authentication/sign-in` | `SignInCommand`; `IUserCommandService.SignInAsync` | `SignInResource`; `SignInCommandFromResourceAssembler`; `AuthenticatedUserResourceFromEntityAssembler` |
| CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items` | `GetAllCatalogItemsQuery`; `ICatalogItemQueryService.Handle` | `CatalogItemResource`; `CatalogItemResourceFromEntityAssembler` |
| CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/{id:int}` | `GetCatalogItemByIdQuery`; `ICatalogItemQueryService.Handle` | `CatalogItemResource`; `CatalogItemResourceFromEntityAssembler` |
| CatalogManagement | CatalogItemsController | POST | `/api/v1/catalog-items` | `CreateCatalogItemCommand`; `ICatalogItemCommandService.CreateAsync` | `CreateCatalogItemResource`; `CreateCatalogItemCommandFromResourceAssembler`; `CatalogItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items` | `GetAllInventoryItemsQuery`; `IInventoryItemQueryService.Handle` | `InventoryItemResource`; `InventoryItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/{id:int}` | `GetInventoryItemByIdQuery`; `IInventoryItemQueryService.Handle` | `InventoryItemResource`; `InventoryItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/by-catalog-item/{catalogItemId}` | `GetInventoryItemByCatalogItemIdQuery`; `IInventoryItemQueryService.Handle` | `InventoryItemResource`; `InventoryItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items` | `CreateInventoryItemCommand`; `IInventoryItemCommandService.CreateAsync` | `CreateInventoryItemResource`; `CreateInventoryItemCommandFromResourceAssembler`; `InventoryItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items/{id:int}/reserve` | `ReserveInventoryCommand`; `IInventoryItemCommandService.ReserveAsync` | `ReserveInventoryResource`; `InventoryItemResourceFromEntityAssembler` |
| Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items/{id:int}/release-reservation` | `ReleaseInventoryReservationCommand`; `IInventoryItemCommandService.ReleaseAsync` | `ReserveInventoryResource`; `InventoryItemResourceFromEntityAssembler` |
| Sales | OrdersController | GET | `/api/v1/orders` | `GetAllOrdersQuery`; `IOrderQueryService.Handle` | `OrderResource`; `OrderResourceFromEntityAssembler` |
| Sales | OrdersController | GET | `/api/v1/orders/{id:int}` | `GetOrderByIdQuery`; `IOrderQueryService.Handle` | `OrderResource`; `OrderResourceFromEntityAssembler` |
| Sales | OrdersController | POST | `/api/v1/orders` | `CreateOrderCommand`; `IOrderCommandService.CreateAsync` | `CreateOrderResource`; `CreateOrderCommandFromResourceAssembler`; `OrderResourceFromEntityAssembler` |
| Sales | OrdersController | POST | `/api/v1/orders/{id:int}/confirm` | `ConfirmOrderCommand`; `IOrderCommandService.ConfirmAsync` | `ConfirmOrderResource`; `OrderResourceFromEntityAssembler` |
| Sales | OrdersController | POST | `/api/v1/orders/{id:int}/reject` | `RejectOrderCommand`; `IOrderCommandService.RejectAsync` | `RejectOrderResource`; `OrderResourceFromEntityAssembler` |
| Sales | OrdersController | POST | `/api/v1/orders/{id:int}/cancel` | `CancelOrderCommand`; `IOrderCommandService.CancelAsync` | no request resource; `OrderResourceFromEntityAssembler` |
| Logistics | ShipmentsController | GET | `/api/v1/shipments` | `GetAllShipmentsQuery`; `IShipmentQueryService.Handle` | `ShipmentResource`; `ShipmentResourceFromEntityAssembler` |
| Logistics | ShipmentsController | GET | `/api/v1/shipments/{id:int}` | `GetShipmentByIdQuery`; `IShipmentQueryService.Handle` | `ShipmentResource`; `ShipmentResourceFromEntityAssembler` |
| Logistics | ShipmentsController | POST | `/api/v1/shipments` | `ScheduleShipmentCommand`; `IShipmentCommandService.ScheduleAsync` | `ScheduleShipmentResource`; `ScheduleShipmentCommandFromResourceAssembler`; `ShipmentResourceFromEntityAssembler` |
| Logistics | ShipmentsController | POST | `/api/v1/shipments/{id:int}/delivered` | `MarkShipmentDeliveredCommand`; `IShipmentCommandService.MarkDeliveredAsync` | no request resource; `ShipmentResourceFromEntityAssembler` |
| Invoicing | InvoicesController | GET | `/api/v1/invoices` | `GetAllInvoicesQuery`; `IInvoiceQueryService.Handle` | `InvoiceResource`; `InvoiceResourceFromEntityAssembler` |
| Invoicing | InvoicesController | GET | `/api/v1/invoices/{id:int}` | `GetInvoiceByIdQuery`; `IInvoiceQueryService.Handle` | `InvoiceResource`; `InvoiceResourceFromEntityAssembler` |
| Invoicing | InvoicesController | POST | `/api/v1/invoices` | `GenerateInvoiceCommand`; `IInvoiceCommandService.GenerateAsync` | `GenerateInvoiceResource`; `GenerateInvoiceCommandFromResourceAssembler`; `InvoiceResourceFromEntityAssembler` |
| Invoicing | InvoicesController | POST | `/api/v1/invoices/{id:int}/paid` | `MarkInvoicePaidCommand`; `IInvoiceCommandService.MarkPaidAsync` | no request resource; `InvoiceResourceFromEntityAssembler` |

## Missing Endpoints For Existing Resources

| Existing resource | Missing useful endpoints | Why useful for AV2 |
|---|---|---|
| `CatalogItem` | `PUT /catalog-items/{id:int}`, `DELETE /catalog-items/{id:int}`, `GET /catalog-items/by-catalog-item/{catalogItemId}`, filters by category, brand, cold-chain requirement, active status | Catalog needs admin maintenance, public filtering, and stable business identifier lookup. `UpdateCatalogItemCommand` already exists but is not exposed. |
| `InventoryItem` | `PUT /inventory-items/{id:int}`, `DELETE /inventory-items/{id:int}`, filters by warehouse, low stock, product/catalog item, domain actions for stock adjustment and movement registration | Warehouse flow needs stock visibility by location and traceable inventory changes. |
| `Order` | `GET /orders/by-customer/{customerId}`, `GET /orders/by-status/{status}`, `GET /orders/by-order-number/{orderNumber}`, `PUT /orders/{id:int}`, order-item subresource endpoints | Sales flow needs buyer/order tracking and before-confirmation order maintenance. |
| `Shipment` | `GET /shipments/by-order/{orderId:int}`, `GET /shipments/by-status/{status}`, `PUT /shipments/{id:int}/schedule`, `POST /shipments/{id:int}/temperature`, tracking-event endpoints, delivery-route assignment | Cold-chain logistics needs operational status, schedule changes, route visibility, and temperature traceability. |
| `Invoice` | `GET /invoices/by-order/{orderId:int}`, `GET /invoices/by-status/{paymentStatus}`, `PUT /invoices/{id:int}`, `POST /invoices/{id:int}/void`, payment resource endpoints | Invoicing needs payment status, order reconciliation, and financial lifecycle actions. |
| `User` | `GET /users`, `GET /users/{id:int}`, `GET /users/by-username/{username}`, role update, deactivate/delete | IAM currently exposes auth only. AV2 needs user administration for operators, commercial users, and buyers. |

## Proposed New Resources For AV2

| Bounded context | Resource | Rationale |
|---|---|---|
| CatalogManagement | Categories | Needed to classify catalog items and power buyer portal filtering. |
| CatalogManagement | Brands | Needed for imported product identity and commercial filtering. |
| Warehouse | Warehouses | Needed to model cold-chain storage locations instead of plain text locations only. |
| Warehouse | InventoryMovements | Needed for audit trail of stock entries, exits, reservations, releases, and adjustments. |
| Sales | Customers | Needed to represent B2B buyers and support customer-specific order history. |
| Sales | OrderItems | Already exists as entity under `Order`; expose as nested order resource for item-level operations where useful. |
| Logistics | ShipmentTrackingEvents | Needed for traceability of shipment state, location, and temperature events. |
| Logistics | DeliveryRoutes | Needed for planned delivery routes and route assignment to shipments. |
| Invoicing | Payments | Needed to separate invoice generation from payment registration and confirmation. |
| Iam | Users | Needed for platform user administration beyond sign-up/sign-in. |

## Target Endpoint Plan

Target count: 75 endpoints total. This table includes current endpoints plus planned additions.

| # | Bounded context | Controller | Method | Route | Command or query needed | Service needed | Repository needed | Resource needed | Assembler needed | Priority |
|---:|---|---|---:|---|---|---|---|---|---|---|
| 1 | Iam | AuthenticationController | POST | `/api/v1/authentication/sign-up` | `SignUpCommand` | `IUserCommandService` | `IUserRepository` | `SignUpResource`, `AuthenticatedUserResource` | `SignUpCommandFromResourceAssembler`, `AuthenticatedUserResourceFromEntityAssembler` | P0 |
| 2 | Iam | AuthenticationController | POST | `/api/v1/authentication/sign-in` | `SignInCommand` | `IUserCommandService` | `IUserRepository` | `SignInResource`, `AuthenticatedUserResource` | `SignInCommandFromResourceAssembler`, `AuthenticatedUserResourceFromEntityAssembler` | P0 |
| 3 | Iam | UsersController | GET | `/api/v1/users` | `GetAllUsersQuery` | `IUserQueryService` | `IUserRepository` | `UserResource` | `UserResourceFromEntityAssembler` | P1 |
| 4 | Iam | UsersController | GET | `/api/v1/users/{id:int}` | `GetUserByIdQuery` | `IUserQueryService` | `IUserRepository` | `UserResource` | `UserResourceFromEntityAssembler` | P1 |
| 5 | Iam | UsersController | GET | `/api/v1/users/by-username/{username}` | `GetUserByUsernameQuery` | `IUserQueryService` | `IUserRepository` | `UserResource` | `UserResourceFromEntityAssembler` | P1 |
| 6 | Iam | UsersController | PUT | `/api/v1/users/{id:int}/role` | `UpdateUserRoleCommand` | `IUserCommandService` | `IUserRepository` | `UpdateUserRoleResource`, `UserResource` | `UpdateUserRoleCommandFromResourceAssembler`, `UserResourceFromEntityAssembler` | P2 |
| 7 | Iam | UsersController | DELETE | `/api/v1/users/{id:int}` | `DeactivateUserCommand` | `IUserCommandService` | `IUserRepository` | `UserResource` | `UserResourceFromEntityAssembler` | P2 |
| 8 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items` | `GetAllCatalogItemsQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P0 |
| 9 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/{id:int}` | `GetCatalogItemByIdQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P0 |
| 10 | CatalogManagement | CatalogItemsController | POST | `/api/v1/catalog-items` | `CreateCatalogItemCommand` | `ICatalogItemCommandService` | `ICatalogItemRepository` | `CreateCatalogItemResource`, `CatalogItemResource` | `CreateCatalogItemCommandFromResourceAssembler`, `CatalogItemResourceFromEntityAssembler` | P0 |
| 11 | CatalogManagement | CatalogItemsController | PUT | `/api/v1/catalog-items/{id:int}` | `UpdateCatalogItemCommand` | `ICatalogItemCommandService` | `ICatalogItemRepository` | `UpdateCatalogItemResource`, `CatalogItemResource` | `UpdateCatalogItemCommandFromResourceAssembler`, `CatalogItemResourceFromEntityAssembler` | P0 |
| 12 | CatalogManagement | CatalogItemsController | DELETE | `/api/v1/catalog-items/{id:int}` | `DeactivateCatalogItemCommand` | `ICatalogItemCommandService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P0 |
| 13 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/by-catalog-item/{catalogItemId}` | `GetCatalogItemByCatalogItemIdQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P0 |
| 14 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/by-category/{categoryId}` | `GetCatalogItemsByCategoryQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P1 |
| 15 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/by-brand/{brandId}` | `GetCatalogItemsByBrandQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P1 |
| 16 | CatalogManagement | CatalogItemsController | GET | `/api/v1/catalog-items/cold-chain/{requirement}` | `GetCatalogItemsByColdChainRequirementQuery` | `ICatalogItemQueryService` | `ICatalogItemRepository` | `CatalogItemResource` | `CatalogItemResourceFromEntityAssembler` | P1 |
| 17 | CatalogManagement | CategoriesController | GET | `/api/v1/categories` | `GetAllCategoriesQuery` | `ICategoryQueryService` | `ICategoryRepository` | `CategoryResource` | `CategoryResourceFromEntityAssembler` | P1 |
| 18 | CatalogManagement | CategoriesController | POST | `/api/v1/categories` | `CreateCategoryCommand` | `ICategoryCommandService` | `ICategoryRepository` | `CreateCategoryResource`, `CategoryResource` | `CreateCategoryCommandFromResourceAssembler`, `CategoryResourceFromEntityAssembler` | P1 |
| 19 | CatalogManagement | BrandsController | GET | `/api/v1/brands` | `GetAllBrandsQuery` | `IBrandQueryService` | `IBrandRepository` | `BrandResource` | `BrandResourceFromEntityAssembler` | P1 |
| 20 | CatalogManagement | BrandsController | POST | `/api/v1/brands` | `CreateBrandCommand` | `IBrandCommandService` | `IBrandRepository` | `CreateBrandResource`, `BrandResource` | `CreateBrandCommandFromResourceAssembler`, `BrandResourceFromEntityAssembler` | P1 |
| 21 | Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items` | `GetAllInventoryItemsQuery` | `IInventoryItemQueryService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P0 |
| 22 | Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/{id:int}` | `GetInventoryItemByIdQuery` | `IInventoryItemQueryService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P0 |
| 23 | Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/by-catalog-item/{catalogItemId}` | `GetInventoryItemByCatalogItemIdQuery` | `IInventoryItemQueryService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P0 |
| 24 | Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items` | `CreateInventoryItemCommand` | `IInventoryItemCommandService` | `IInventoryItemRepository` | `CreateInventoryItemResource`, `InventoryItemResource` | `CreateInventoryItemCommandFromResourceAssembler`, `InventoryItemResourceFromEntityAssembler` | P0 |
| 25 | Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items/{id:int}/reserve` | `ReserveInventoryCommand` | `IInventoryItemCommandService` | `IInventoryItemRepository` | `ReserveInventoryResource`, `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P0 |
| 26 | Warehouse | InventoryItemsController | POST | `/api/v1/inventory-items/{id:int}/release-reservation` | `ReleaseInventoryReservationCommand` | `IInventoryItemCommandService` | `IInventoryItemRepository` | `ReserveInventoryResource`, `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P0 |
| 27 | Warehouse | InventoryItemsController | PUT | `/api/v1/inventory-items/{id:int}` | `UpdateInventoryItemCommand` | `IInventoryItemCommandService` | `IInventoryItemRepository` | `UpdateInventoryItemResource`, `InventoryItemResource` | `UpdateInventoryItemCommandFromResourceAssembler`, `InventoryItemResourceFromEntityAssembler` | P0 |
| 28 | Warehouse | InventoryItemsController | DELETE | `/api/v1/inventory-items/{id:int}` | `ArchiveInventoryItemCommand` | `IInventoryItemCommandService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P1 |
| 29 | Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/by-warehouse/{warehouseId}` | `GetInventoryItemsByWarehouseQuery` | `IInventoryItemQueryService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P1 |
| 30 | Warehouse | InventoryItemsController | GET | `/api/v1/inventory-items/low-stock` | `GetLowStockInventoryItemsQuery` | `IInventoryItemQueryService` | `IInventoryItemRepository` | `InventoryItemResource` | `InventoryItemResourceFromEntityAssembler` | P1 |
| 31 | Warehouse | WarehousesController | GET | `/api/v1/warehouses` | `GetAllWarehousesQuery` | `IWarehouseQueryService` | `IWarehouseRepository` | `WarehouseResource` | `WarehouseResourceFromEntityAssembler` | P1 |
| 32 | Warehouse | WarehousesController | POST | `/api/v1/warehouses` | `CreateWarehouseCommand` | `IWarehouseCommandService` | `IWarehouseRepository` | `CreateWarehouseResource`, `WarehouseResource` | `CreateWarehouseCommandFromResourceAssembler`, `WarehouseResourceFromEntityAssembler` | P1 |
| 33 | Warehouse | InventoryMovementsController | GET | `/api/v1/inventory-movements` | `GetAllInventoryMovementsQuery` | `IInventoryMovementQueryService` | `IInventoryMovementRepository` | `InventoryMovementResource` | `InventoryMovementResourceFromEntityAssembler` | P2 |
| 34 | Warehouse | InventoryMovementsController | POST | `/api/v1/inventory-movements` | `RegisterInventoryMovementCommand` | `IInventoryMovementCommandService` | `IInventoryMovementRepository` | `RegisterInventoryMovementResource`, `InventoryMovementResource` | `RegisterInventoryMovementCommandFromResourceAssembler`, `InventoryMovementResourceFromEntityAssembler` | P2 |
| 35 | Sales | OrdersController | GET | `/api/v1/orders` | `GetAllOrdersQuery` | `IOrderQueryService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 36 | Sales | OrdersController | GET | `/api/v1/orders/{id:int}` | `GetOrderByIdQuery` | `IOrderQueryService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 37 | Sales | OrdersController | POST | `/api/v1/orders` | `CreateOrderCommand` | `IOrderCommandService` | `IOrderRepository` | `CreateOrderResource`, `OrderResource` | `CreateOrderCommandFromResourceAssembler`, `OrderResourceFromEntityAssembler` | P0 |
| 38 | Sales | OrdersController | POST | `/api/v1/orders/{id:int}/confirm` | `ConfirmOrderCommand` | `IOrderCommandService` | `IOrderRepository` | `ConfirmOrderResource`, `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 39 | Sales | OrdersController | POST | `/api/v1/orders/{id:int}/reject` | `RejectOrderCommand` | `IOrderCommandService` | `IOrderRepository` | `RejectOrderResource`, `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 40 | Sales | OrdersController | POST | `/api/v1/orders/{id:int}/cancel` | `CancelOrderCommand` | `IOrderCommandService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 41 | Sales | OrdersController | GET | `/api/v1/orders/by-customer/{customerId}` | `GetOrdersByCustomerQuery` | `IOrderQueryService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 42 | Sales | OrdersController | GET | `/api/v1/orders/by-status/{status}` | `GetOrdersByStatusQuery` | `IOrderQueryService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 43 | Sales | OrdersController | GET | `/api/v1/orders/by-order-number/{orderNumber}` | `GetOrderByOrderNumberQuery` | `IOrderQueryService` | `IOrderRepository` | `OrderResource` | `OrderResourceFromEntityAssembler` | P0 |
| 44 | Sales | OrdersController | PUT | `/api/v1/orders/{id:int}` | `UpdateOrderCommand` | `IOrderCommandService` | `IOrderRepository` | `UpdateOrderResource`, `OrderResource` | `UpdateOrderCommandFromResourceAssembler`, `OrderResourceFromEntityAssembler` | P1 |
| 45 | Sales | CustomersController | GET | `/api/v1/customers` | `GetAllCustomersQuery` | `ICustomerQueryService` | `ICustomerRepository` | `CustomerResource` | `CustomerResourceFromEntityAssembler` | P1 |
| 46 | Sales | CustomersController | GET | `/api/v1/customers/{id:int}` | `GetCustomerByIdQuery` | `ICustomerQueryService` | `ICustomerRepository` | `CustomerResource` | `CustomerResourceFromEntityAssembler` | P1 |
| 47 | Sales | CustomersController | POST | `/api/v1/customers` | `CreateCustomerCommand` | `ICustomerCommandService` | `ICustomerRepository` | `CreateCustomerResource`, `CustomerResource` | `CreateCustomerCommandFromResourceAssembler`, `CustomerResourceFromEntityAssembler` | P1 |
| 48 | Sales | OrderItemsController | GET | `/api/v1/orders/{orderId:int}/items` | `GetOrderItemsByOrderQuery` | `IOrderItemQueryService` | `IOrderRepository` | `OrderItemResource` | `OrderItemResourceFromEntityAssembler` | P2 |
| 49 | Sales | OrderItemsController | POST | `/api/v1/orders/{orderId:int}/items` | `AddOrderItemCommand` | `IOrderItemCommandService` | `IOrderRepository` | `CreateOrderItemResource`, `OrderItemResource` | `CreateOrderItemCommandFromResourceAssembler`, `OrderItemResourceFromEntityAssembler` | P2 |
| 50 | Logistics | ShipmentsController | GET | `/api/v1/shipments` | `GetAllShipmentsQuery` | `IShipmentQueryService` | `IShipmentRepository` | `ShipmentResource` | `ShipmentResourceFromEntityAssembler` | P0 |
| 51 | Logistics | ShipmentsController | GET | `/api/v1/shipments/{id:int}` | `GetShipmentByIdQuery` | `IShipmentQueryService` | `IShipmentRepository` | `ShipmentResource` | `ShipmentResourceFromEntityAssembler` | P0 |
| 52 | Logistics | ShipmentsController | POST | `/api/v1/shipments` | `ScheduleShipmentCommand` | `IShipmentCommandService` | `IShipmentRepository` | `ScheduleShipmentResource`, `ShipmentResource` | `ScheduleShipmentCommandFromResourceAssembler`, `ShipmentResourceFromEntityAssembler` | P0 |
| 53 | Logistics | ShipmentsController | POST | `/api/v1/shipments/{id:int}/delivered` | `MarkShipmentDeliveredCommand` | `IShipmentCommandService` | `IShipmentRepository` | `ShipmentResource` | `ShipmentResourceFromEntityAssembler` | P0 |
| 54 | Logistics | ShipmentsController | GET | `/api/v1/shipments/by-order/{orderId:int}` | `GetShipmentsByOrderQuery` | `IShipmentQueryService` | `IShipmentRepository` | `ShipmentResource` | `ShipmentResourceFromEntityAssembler` | P0 |
| 55 | Logistics | ShipmentsController | GET | `/api/v1/shipments/by-status/{status}` | `GetShipmentsByStatusQuery` | `IShipmentQueryService` | `IShipmentRepository` | `ShipmentResource` | `ShipmentResourceFromEntityAssembler` | P0 |
| 56 | Logistics | ShipmentsController | PUT | `/api/v1/shipments/{id:int}/schedule` | `RescheduleShipmentCommand` | `IShipmentCommandService` | `IShipmentRepository` | `RescheduleShipmentResource`, `ShipmentResource` | `RescheduleShipmentCommandFromResourceAssembler`, `ShipmentResourceFromEntityAssembler` | P1 |
| 57 | Logistics | ShipmentsController | POST | `/api/v1/shipments/{id:int}/temperature` | `RegisterShipmentTemperatureCommand` | `IShipmentCommandService` | `IShipmentRepository` | `RegisterShipmentTemperatureResource`, `ShipmentResource` | `RegisterShipmentTemperatureCommandFromResourceAssembler`, `ShipmentResourceFromEntityAssembler` | P2 |
| 58 | Logistics | ShipmentTrackingEventsController | GET | `/api/v1/shipments/{shipmentId:int}/tracking-events` | `GetTrackingEventsByShipmentQuery` | `IShipmentTrackingEventQueryService` | `IShipmentTrackingEventRepository` | `ShipmentTrackingEventResource` | `ShipmentTrackingEventResourceFromEntityAssembler` | P1 |
| 59 | Logistics | ShipmentTrackingEventsController | POST | `/api/v1/shipments/{shipmentId:int}/tracking-events` | `CreateShipmentTrackingEventCommand` | `IShipmentTrackingEventCommandService` | `IShipmentTrackingEventRepository` | `CreateShipmentTrackingEventResource`, `ShipmentTrackingEventResource` | `CreateShipmentTrackingEventCommandFromResourceAssembler`, `ShipmentTrackingEventResourceFromEntityAssembler` | P1 |
| 60 | Logistics | ShipmentTrackingEventsController | GET | `/api/v1/shipment-tracking-events/{id:int}` | `GetShipmentTrackingEventByIdQuery` | `IShipmentTrackingEventQueryService` | `IShipmentTrackingEventRepository` | `ShipmentTrackingEventResource` | `ShipmentTrackingEventResourceFromEntityAssembler` | P2 |
| 61 | Logistics | DeliveryRoutesController | GET | `/api/v1/delivery-routes` | `GetAllDeliveryRoutesQuery` | `IDeliveryRouteQueryService` | `IDeliveryRouteRepository` | `DeliveryRouteResource` | `DeliveryRouteResourceFromEntityAssembler` | P2 |
| 62 | Logistics | DeliveryRoutesController | POST | `/api/v1/delivery-routes` | `CreateDeliveryRouteCommand` | `IDeliveryRouteCommandService` | `IDeliveryRouteRepository` | `CreateDeliveryRouteResource`, `DeliveryRouteResource` | `CreateDeliveryRouteCommandFromResourceAssembler`, `DeliveryRouteResourceFromEntityAssembler` | P2 |
| 63 | Invoicing | InvoicesController | GET | `/api/v1/invoices` | `GetAllInvoicesQuery` | `IInvoiceQueryService` | `IInvoiceRepository` | `InvoiceResource` | `InvoiceResourceFromEntityAssembler` | P0 |
| 64 | Invoicing | InvoicesController | GET | `/api/v1/invoices/{id:int}` | `GetInvoiceByIdQuery` | `IInvoiceQueryService` | `IInvoiceRepository` | `InvoiceResource` | `InvoiceResourceFromEntityAssembler` | P0 |
| 65 | Invoicing | InvoicesController | POST | `/api/v1/invoices` | `GenerateInvoiceCommand` | `IInvoiceCommandService` | `IInvoiceRepository` | `GenerateInvoiceResource`, `InvoiceResource` | `GenerateInvoiceCommandFromResourceAssembler`, `InvoiceResourceFromEntityAssembler` | P0 |
| 66 | Invoicing | InvoicesController | POST | `/api/v1/invoices/{id:int}/paid` | `MarkInvoicePaidCommand` | `IInvoiceCommandService` | `IInvoiceRepository` | `InvoiceResource` | `InvoiceResourceFromEntityAssembler` | P0 |
| 67 | Invoicing | InvoicesController | GET | `/api/v1/invoices/by-order/{orderId:int}` | `GetInvoicesByOrderQuery` | `IInvoiceQueryService` | `IInvoiceRepository` | `InvoiceResource` | `InvoiceResourceFromEntityAssembler` | P0 |
| 68 | Invoicing | InvoicesController | GET | `/api/v1/invoices/by-status/{paymentStatus}` | `GetInvoicesByPaymentStatusQuery` | `IInvoiceQueryService` | `IInvoiceRepository` | `InvoiceResource` | `InvoiceResourceFromEntityAssembler` | P0 |
| 69 | Invoicing | InvoicesController | PUT | `/api/v1/invoices/{id:int}` | `UpdateInvoiceCommand` | `IInvoiceCommandService` | `IInvoiceRepository` | `UpdateInvoiceResource`, `InvoiceResource` | `UpdateInvoiceCommandFromResourceAssembler`, `InvoiceResourceFromEntityAssembler` | P1 |
| 70 | Invoicing | InvoicesController | POST | `/api/v1/invoices/{id:int}/void` | `VoidInvoiceCommand` | `IInvoiceCommandService` | `IInvoiceRepository` | `VoidInvoiceResource`, `InvoiceResource` | `VoidInvoiceCommandFromResourceAssembler`, `InvoiceResourceFromEntityAssembler` | P2 |
| 71 | Invoicing | PaymentsController | GET | `/api/v1/payments` | `GetAllPaymentsQuery` | `IPaymentQueryService` | `IPaymentRepository` | `PaymentResource` | `PaymentResourceFromEntityAssembler` | P1 |
| 72 | Invoicing | PaymentsController | GET | `/api/v1/payments/{id:int}` | `GetPaymentByIdQuery` | `IPaymentQueryService` | `IPaymentRepository` | `PaymentResource` | `PaymentResourceFromEntityAssembler` | P1 |
| 73 | Invoicing | PaymentsController | POST | `/api/v1/payments` | `RegisterPaymentCommand` | `IPaymentCommandService` | `IPaymentRepository` | `RegisterPaymentResource`, `PaymentResource` | `RegisterPaymentCommandFromResourceAssembler`, `PaymentResourceFromEntityAssembler` | P1 |
| 74 | Invoicing | PaymentsController | GET | `/api/v1/payments/by-invoice/{invoiceId:int}` | `GetPaymentsByInvoiceQuery` | `IPaymentQueryService` | `IPaymentRepository` | `PaymentResource` | `PaymentResourceFromEntityAssembler` | P1 |
| 75 | Invoicing | PaymentsController | POST | `/api/v1/payments/{id:int}/confirm` | `ConfirmPaymentCommand` | `IPaymentCommandService` | `IPaymentRepository` | `ConfirmPaymentResource`, `PaymentResource` | `ConfirmPaymentCommandFromResourceAssembler`, `PaymentResourceFromEntityAssembler` | P2 |

## Target Count By Bounded Context

| Bounded context | Target endpoints |
|---|---:|
| Iam | 7 |
| CatalogManagement | 13 |
| Warehouse | 14 |
| Sales | 15 |
| Logistics | 13 |
| Invoicing | 13 |
| Total | 75 |

## Implementation Batches

### Batch 1: Complete Existing Resources

Goal: finish CRUD, filters, and simple lookups around resources that already exist.

| Scope | Endpoints |
|---|---|
| CatalogManagement | `CatalogItemsController` update, deactivate, by catalog item id, by category, by brand, by cold-chain requirement |
| Warehouse | `InventoryItemsController` update, archive, by warehouse, low stock |
| Sales | `OrdersController` by customer, by status, by order number, update before confirmation |
| Logistics | `ShipmentsController` by order, by status, reschedule |
| Invoicing | `InvoicesController` by order, by status, update, void |

### Batch 2: Add Simple New Resources

Goal: add low-risk aggregate roots with normal list/create/read flows before complex workflows.

| Scope | Resources |
|---|---|
| CatalogManagement | `Category`, `Brand` |
| Warehouse | `Warehouse` |
| Sales | `Customer` |
| Invoicing | `Payment` initial register/list/get/by-invoice |
| Iam | `UsersController` list/get/by-username |

### Batch 3: Add Domain-Specific Actions

Goal: add actions that express Nexa business workflows.

| Scope | Actions |
|---|---|
| Warehouse | `InventoryMovement` registration and movement history |
| Sales | nested `OrderItems` list/add under `Order` aggregate |
| Logistics | shipment temperature registration, tracking events, delivery routes |
| Invoicing | payment confirmation and invoice voiding |
| Iam | user role update and user deactivation |

### Batch 4: Integration Cleanup

Goal: make endpoints consistent and demonstrable for AV2.

| Scope | Cleanup |
|---|---|
| Routing | Verify kebab-case route names and avoid duplicate routes. |
| Resources | Keep request and response resources separate from aggregates. |
| Assemblers | Add resource-to-command and entity-to-resource assemblers for every new endpoint. |
| Services | Register new command/query services and repositories in each context dependency injection extension. |
| Persistence | Add EF Core configuration and migrations only after model shape is approved. |
| Errors | Wire context-specific errors from `Domain/Model/Errors` into service/controller responses. |
| OpenAPI | Confirm Swagger displays all endpoints with useful summaries and response contracts. |

## Biggest Risks

| Risk | Impact | Mitigation |
|---|---|---|
| `.NET 10` SDK missing locally | Cannot build after AV2 endpoint work. | Install/select .NET 10 SDK before implementation batches. |
| Pomelo provider currently stable at EF Core 9 compatibility | Full EF Core 10 package alignment may be blocked until Pomelo supports it. | Keep provider style for now; avoid changing MySQL provider without approval. |
| New resources may require schema changes | Migrations can grow fast and affect seeded data. | Implement in batches, review aggregate shape before migration. |
| OrderItems as nested entity | Separate repository would violate aggregate boundary. | Use `IOrderRepository` for nested order item operations unless design changes. |
| Cross-context consistency | Orders, inventory, shipments, invoices, and payments can drift. | Keep integration actions explicit and avoid hidden cross-context writes until AV2 flow is agreed. |
