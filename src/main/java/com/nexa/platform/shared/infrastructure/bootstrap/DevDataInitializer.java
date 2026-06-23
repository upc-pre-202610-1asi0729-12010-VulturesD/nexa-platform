package com.nexa.platform.shared.infrastructure.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.domain.model.repositories.*;
import com.nexa.platform.iam.domain.model.*;
import com.nexa.platform.iam.domain.model.repositories.*;
import com.nexa.platform.invoicing.application.outbound.BusinessDocumentContentGenerator;
import com.nexa.platform.invoicing.domain.model.*;
import com.nexa.platform.invoicing.domain.model.repositories.*;
import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import com.nexa.platform.logistics.domain.model.DispatchEvent;
import com.nexa.platform.logistics.domain.model.DispatchOrder;
import com.nexa.platform.logistics.domain.model.ProofOfDeliveryRecord;
import com.nexa.platform.logistics.domain.model.TemperatureLog;
import com.nexa.platform.logistics.domain.model.repositories.DeliveryRouteRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.DispatchEventRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.DispatchOrderRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.ProofOfDeliveryRecordRepositoryPort;
import com.nexa.platform.logistics.domain.model.repositories.TemperatureLogRepositoryPort;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.domain.model.repositories.*;
import com.nexa.platform.shared.domain.model.DocumentType;
import com.nexa.platform.shared.domain.repositories.DocumentTypeRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.Tenant;
import com.nexa.platform.tenantmanagement.domain.model.UserWorkspaceMembership;
import com.nexa.platform.tenantmanagement.domain.model.Workspace;
import com.nexa.platform.tenantmanagement.domain.model.repositories.TenantRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.UserWorkspaceMembershipRepositoryPort;
import com.nexa.platform.tenantmanagement.domain.model.repositories.WorkspaceRepositoryPort;
import com.nexa.platform.warehouse.domain.model.*;
import com.nexa.platform.warehouse.domain.model.repositories.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nexa.platform.promotions.domain.model.aggregates.Promotion;
import com.nexa.platform.promotions.domain.model.repositories.PromotionRepository;

@Configuration
@Profile({"local", "test", "seed"})
public class DevDataInitializer {
    private static final String WORKSPACE_PROFILE_PASSWORD = "NexaAccess2026!";
    private static final String PRIMARY_WAREHOUSE_LOCATION = "Av. Guillermo Dansey 2026, Cercado de Lima";

    @Bean
    CommandLineRunner seed(RoleRepositoryPort roles, UserAccountRepositoryPort users, PasswordEncoder encoder,
                           CategoryRepositoryPort categories, ProductRepositoryPort products,
                           BrandRepositoryPort brands,
                           WarehouseRepositoryPort warehouses, InventoryItemRepositoryPort inventory,
                           StockBatchRepositoryPort stockBatches,
                           InventoryReservationRepositoryPort inventoryReservations,
                           CustomerRepositoryPort customers, DeliveryRouteRepositoryPort routes,
                           DispatchOrderRepositoryPort dispatchOrders,
                           DispatchEventRepositoryPort dispatchEvents,
                           ProofOfDeliveryRecordRepositoryPort proofsOfDelivery,
                           TemperatureLogRepositoryPort temperatureLogs,
                           SalesOrderRepositoryPort orders, PurchaseRequestRepositoryPort purchaseRequests,
                           InvoiceRepositoryPort invoices, PaymentRepositoryPort payments,
                           PaymentProcessRecordRepositoryPort paymentProcesses,
                           PaymentMethodRecordRepositoryPort paymentMethods,
                           BusinessDocumentRepositoryPort businessDocuments,
                           DocumentTypeRepositoryPort documentTypes,
                           TenantRepositoryPort tenants, WorkspaceRepositoryPort workspaces,
                           UserWorkspaceMembershipRepositoryPort memberships,
                           BusinessDocumentContentGenerator documentContentGenerator,
                           PromotionRepository promotions,
                           ObjectMapper objectMapper) {
        return args -> {
            Role admin = role(roles, RoleName.ROLE_ADMIN);
            Role operator = role(roles, RoleName.ROLE_OPERATOR);
            Role sales = role(roles, RoleName.ROLE_SALES);
            Role logistics = role(roles, RoleName.ROLE_LOGISTICS);
            Role warehouseRole = role(roles, RoleName.ROLE_WAREHOUSE);
            Role buyer = role(roles, RoleName.ROLE_BUYER);

            UserAccount adminUser = user(users, encoder, "Nexa Admin", "admin@nexa.local", "NexaAdmin123", admin);
            UserAccount operatorUser = user(users, encoder, "Nexa Logistics Operator", "operator@nexa.local", "Operator123", operator);
            UserAccount localBuyer = user(users, encoder, "Nexa Buyer Portal", "buyer@nexa.local", "Buyer123", buyer);
            UserAccount salesUser = user(users, encoder, "Valeria Sanchez", "sales@nexa.com", WORKSPACE_PROFILE_PASSWORD, sales);
            UserAccount logisticsUser = user(users, encoder, "Roberto Garcia", "logistics@nexa.com", WORKSPACE_PROFILE_PASSWORD, logistics);
            UserAccount warehouseUser = user(users, encoder, "Joaquin Verde", "warehouse@nexa.com", WORKSPACE_PROFILE_PASSWORD, warehouseRole);
            UserAccount buyerUser = user(users, encoder, "Elena Litano", "buyer@nexa.com", WORKSPACE_PROFILE_PASSWORD, buyer);

            UserAccount carlosRios = user(users, encoder, "Carlos Rios", "carlos.rios@icisa.pe", "Password123!", admin);
            UserAccount valeriaSanchez = user(users, encoder, "Valeria Sanchez", "valeria.sanchez@icisa.pe", "Password123!", sales);
            UserAccount robertoGarcia = user(users, encoder, "Roberto Garcia", "roberto.garcia@icisa.pe", "Password123!", logistics);
            UserAccount elenaLitano = user(users, encoder, "Elena Litano", "elena.litano@icisa.pe", "Password123!", buyer);

            Product primaryProduct = seedCatalog(categories, products, objectMapper);
            seedBrands(brands, products);
            Warehouse warehouse = warehouses.findAll().stream().findFirst().orElseGet(() -> warehouses.save(new Warehouse(PRIMARY_WAREHOUSE_LOCATION, PRIMARY_WAREHOUSE_LOCATION, TemperatureBand.FROZEN)));
            List<Product> allProducts = products.findByActiveTrueOrderByIdAsc();
            InventoryItem primaryInventory = null;
            for (Product p : allProducts) {
                final Product fp = p;
                InventoryItem item = inventory.findByTenantIdOrderByIdAsc(1L).stream()
                    .filter(ii -> ii.getProduct().getSku().equals(fp.getSku()))
                    .findFirst()
                    .orElseGet(() -> inventory.save(new InventoryItem(warehouse, fp, 500, 30)));
                if (primaryInventory == null) primaryInventory = item;
                
                String lotCode = "LOT-" + p.getSku() + "-01";
                if (stockBatches.findByTenantIdAndLotCode(1L, lotCode).isEmpty()) {
                    stockBatches.save(new StockBatch(1L, item, lotCode, 500, 0,
                        LocalDate.now(), LocalDate.now().plusDays(90), "CHILLED-A", "active",
                        new BigDecimal("2.00"), new BigDecimal("8.00")));
                }
            }
            seedWarehouseOperations(primaryInventory, inventory, stockBatches, inventoryReservations);

            Customer sourceCustomer = sourceCustomer(customers);
            Customer secondaryCustomer = secondaryCustomer(customers);
            seedWorkspaceSessions(tenants, workspaces, memberships, sourceCustomer, adminUser, operatorUser,
                localBuyer, salesUser, logisticsUser, warehouseUser, buyerUser,
                carlosRios, valeriaSanchez, robertoGarcia, elenaLitano);
            seedSourceBuyerOrderAndInvoice(products, orders, invoices, payments, sourceCustomer, secondaryCustomer, primaryProduct);
            seedBusinessDocuments(orders, businessDocuments, documentTypes, documentContentGenerator);
            PaymentMethodRecord defaultPaymentMethod = seedPaymentMethods(sourceCustomer, paymentMethods);
            seedPaymentProcesses(orders, payments, paymentProcesses, defaultPaymentMethod);
            seedPurchaseRequests(products, purchaseRequests, primaryProduct);
            if (routes.findAll().isEmpty()) routes.save(new DeliveryRoute("Lima Norte cold route", PRIMARY_WAREHOUSE_LOCATION, "Los Olivos distribution zone"));
            DispatchOrder dispatchOrder = seedDispatchOrders(orders, dispatchOrders);
            seedOperationalRecords(dispatchOrder, dispatchEvents, proofsOfDelivery, temperatureLogs);

            if (promotions.findAll().isEmpty()) {
                promotions.save(new Promotion(1L, "PROMO-COLD-001", "Chilled cheese rotation", "",
                    "Commercial bundle for selected cheese lines with short route windows.",
                    "8% commercial adjustment", "buyer_portal", "", "", "", "Subject to stock and credit validation.",
                    "all", null, null, "active", List.of("PROD-0013", "PROD-0014")));
                promotions.save(new Promotion(1L, "PROMO-COLD-002", "Food service charcuterie pack", "",
                    "Bundle support for hotels and restaurants with recurring weekly demand.",
                    "Tiered price", "client_specific", "", "", "", "Available for approved B2B buyers.",
                    "all", null, null, "active", List.of("PROD-0004", "PROD-0005")));
                promotions.save(new Promotion(1L, "PROMO-COLD-003", "Frozen seafood planning", "",
                    "Planning reminder for frozen inventory reservations before weekend dispatch.",
                    "Route priority", "internal", "", "", "", "Operations capacity review required.",
                    "all", null, null, "scheduled", List.of("PROD-0042")));
            }
        };
    }

    private static Role role(RoleRepositoryPort roles, RoleName name) {
        return roles.findByName(name).orElseGet(() -> roles.save(new Role(name)));
    }

    private static UserAccount user(UserAccountRepositoryPort users, PasswordEncoder encoder, String fullName, String email,
                                    String password, Role role) {
        UserAccount account = users.findByEmail(email).orElse(null);
        if (account != null) {
            account.changePasswordHash(encoder.encode(password));
            return users.save(account);
        }
        account = new UserAccount(fullName, email, encoder.encode(password));
        account.addRole(role);
        return users.save(account);
    }

    private static void seedWorkspaceSessions(TenantRepositoryPort tenants, WorkspaceRepositoryPort workspaces,
                                              UserWorkspaceMembershipRepositoryPort memberships, Customer buyerClient,
                                              UserAccount admin, UserAccount operator, UserAccount localBuyer,
                                              UserAccount sales, UserAccount logistics, UserAccount warehouse,
                                              UserAccount buyer,
                                              UserAccount carlosRios, UserAccount valeriaSanchez, UserAccount robertoGarcia, UserAccount elenaLitano) {
        Tenant tenant = tenants.findBySlug("icisa").orElseGet(() -> tenants.save(new Tenant(
            "ICISA Cold Chain", "Importaciones y Comercio Internacional S.A.", "icisa", "20600000001",
            "icisa.nexa.com.pe", "icisa.pe", "Business", "active", "PE")));
        Workspace workspace = workspaces.findBySlug("icisa").orElseGet(() -> workspaces.save(new Workspace(
            tenant.getId(), "ICISA Main Workspace", "icisa", "icisa.nexa.com.pe",
            "icisa.pe", "active", true)));
        membership(memberships, tenant, workspace, admin, "admin", "Administration", "internal", null);
        membership(memberships, tenant, workspace, operator, "operator", "Logistics", "internal", null);
        membership(memberships, tenant, workspace, localBuyer, "buyer", "Purchasing", "buyer", buyerClient.getId());
        membership(memberships, tenant, workspace, sales, "sales", "Sales", "internal", null);
        membership(memberships, tenant, workspace, logistics, "logistics", "Logistics", "internal", null);
        membership(memberships, tenant, workspace, warehouse, "warehouse", "Warehouse", "internal", null);
        membership(memberships, tenant, workspace, buyer, "buyer", "Purchasing", "buyer", buyerClient.getId());

        membership(memberships, tenant, workspace, carlosRios, "Company Owner", "Executive Office", "internal", null);
        membership(memberships, tenant, workspace, valeriaSanchez, "Operator", "Sales", "internal", null);
        membership(memberships, tenant, workspace, robertoGarcia, "Logistics Manager", "Logistics", "internal", null);
        membership(memberships, tenant, workspace, elenaLitano, "Buyer", "Purchasing", "buyer", buyerClient.getId());
    }

    private static void membership(UserWorkspaceMembershipRepositoryPort memberships, Tenant tenant,
                                   Workspace workspace, UserAccount user, String role, String department,
                                   String portalAccess, Long clientAccountId) {
        UserWorkspaceMembership m = memberships.findByUserIdAndWorkspaceIdAndStatus(user.getId(), workspace.getId(), "active").orElse(null);
        if (m == null) {
            memberships.save(new UserWorkspaceMembership(tenant.getId(), workspace.getId(), user.getId(),
                user.getEmail(), user.getFullName(), role, department, "active", portalAccess, clientAccountId));
        } else {
            m.update(tenant.getId(), workspace.getId(), user.getId(), user.getEmail(), user.getFullName(),
                role, department, "active", portalAccess, clientAccountId);
            memberships.save(m);
        }
    }

    private static Category category(CategoryRepositoryPort categories, String name, String description) {
        return categories.findByName(name).orElseGet(() -> categories.save(new Category(name, description)));
    }

    private static void seedBrands(BrandRepositoryPort brands, ProductRepositoryPort products) {
        products.findByActiveTrueOrderByIdAsc().stream()
            .map(Product::getSupplierName)
            .filter(name -> name != null && !name.isBlank())
            .distinct()
            .forEach(name -> {
                if (!brands.existsByNameIgnoreCase(name)) {
                    brands.save(new Brand(name, "Source-aligned catalog brand for Nexa B2B operations"));
                }
            });
    }

    private static Product seedCatalog(CategoryRepositoryPort categories, ProductRepositoryPort products, ObjectMapper objectMapper) throws IOException {
        List<CatalogSeedItem> items = objectMapper.readValue(new ClassPathResource("data/catalog-items.json").getInputStream(), new TypeReference<>() { });
        Product primaryProduct = null;
        for (CatalogSeedItem item : items) {
            Category category = category(categories, item.category(), "Source-aligned " + item.category() + " category for Nexa B2B catalog operations");
            Product product = product(products, item.sku(), item.name(), item.description(), category, item.brand(), item.price(),
                item.unit(), item.imageUrl(), item.stock(), item.reserved(), item.minStock(), 2, 8, "Keep refrigerated between 2C and 8C");
            if (primaryProduct == null) primaryProduct = product;
        }
        if (primaryProduct == null) throw new IllegalStateException("Catalog seed data is empty");
        return primaryProduct;
    }

    private static Product product(ProductRepositoryPort products, String sku, String name, String description, Category category,
                                   String supplierName, BigDecimal unitPrice, String unit, String imageUrl,
                                   Integer availableStock, Integer reservedStock, Integer minStock,
                                   Integer minCelsius, Integer maxCelsius, String handlingNotes) {
        return products.findBySku(sku).orElseGet(() -> products.save(new Product(sku, name, description, category, supplierName,
            unitPrice, unit, imageUrl, availableStock, reservedStock, minStock, new ColdChainRequirement(minCelsius, maxCelsius, handlingNotes))));
    }

    private static Customer sourceCustomer(CustomerRepositoryPort customers) {
        return customers.findAll().stream()
            .filter(customer -> "20600000001".equals(customer.getTaxId()))
            .findFirst()
            .orElseGet(() -> customers.save(new Customer(1L, "CLI-001",
                "Importaciones y Comercio Internacional S.A.", "ICISA",
                "20600000001", "B2B cold-chain importer", "Elena Litano", "compras@icisa.pe",
                "+51 999 100 100", "Av. Argentina 2450, Callao", "Callao", "Callao",
                "Dock 4 refrigerated reception", "ruc_factura_xml_pdf_guia", "credit_15",
                new BigDecimal("50000.00"), new BigDecimal("12500.00"), "ok",
                "morning_delivery", true, "sales@nexa.com", "active")));
    }

    private static void seedWarehouseOperations(InventoryItem item, InventoryItemRepositoryPort inventory,
                                                StockBatchRepositoryPort stockBatches,
                                                InventoryReservationRepositoryPort reservations) {
        StockBatch lot = stockBatches.findByTenantIdAndLotCode(1L, "LOT-ICISA-001")
            .orElseGet(() -> stockBatches.save(new StockBatch(1L, item, "LOT-ICISA-001", 120, 0,
                LocalDate.now(), LocalDate.now().plusDays(45), "CHILLED-A", "active",
                new BigDecimal("2.00"), new BigDecimal("8.00"))));
        if (!reservations.findByTenantIdOrderByIdDesc(1L).isEmpty()) return;
        item.reserve(10);
        lot.reserve(10);
        inventory.save(item);
        stockBatches.save(lot);
        reservations.save(new InventoryReservationRecord(1L, item, lot, 1L, null,
            "RES-2026-0001", 10));
    }

    private static Customer secondaryCustomer(CustomerRepositoryPort customers) {
        return customers.findAll().stream()
            .filter(customer -> "20601234567".equals(customer.getTaxId()))
            .findFirst()
            .orElseGet(() -> customers.save(new Customer("Distribuidora Norte SAC", "20601234567",
                "compras@dnorte.local", "Av. Los Frigorificos 450")));
    }

    private static void seedSourceBuyerOrderAndInvoice(ProductRepositoryPort products, SalesOrderRepositoryPort orders,
                                                       InvoiceRepositoryPort invoices, PaymentRepositoryPort payments,
                                                       Customer sourceCustomer, Customer secondaryCustomer,
                                                       Product fallbackProduct) {
        SalesOrder sourceOrder = findOrderForCustomer(orders, sourceCustomer);
        SalesOrder sourceSecondOrder = findSecondOrderForCustomer(orders, sourceCustomer);

        if (orders.findAll().isEmpty()) {
            sourceOrder = sourceOrder(products, orders, sourceCustomer, fallbackProduct);
            fillerOrder(products, orders, secondaryCustomer, fallbackProduct, "PROD-0002", 2);
            fillerOrder(products, orders, secondaryCustomer, fallbackProduct, "PROD-0003", 1);
            fillerOrder(products, orders, secondaryCustomer, fallbackProduct, "PROD-0014", 2);
            fillerOrder(products, orders, secondaryCustomer, fallbackProduct, "PROD-0048", 1);
            sourceSecondOrder = sourceSecondOrder(products, orders, sourceCustomer, fallbackProduct);
        }

        if (sourceOrder == null) sourceOrder = sourceOrder(products, orders, sourceCustomer, fallbackProduct);
        if (sourceSecondOrder == null) sourceSecondOrder = sourceSecondOrder(products, orders, sourceCustomer, fallbackProduct);

        if (findInvoice(invoices, "INV-2026-0001") == null) {
            Invoice invoice = new Invoice(sourceOrder, "INV-2026-0001");
            invoice.addLine(new InvoiceLine("MORTADELLA BOLOGNA IGP CON PISTACCHIO MOLDE 7.5KG", 3, new BigDecimal("690.00")));
            invoice.addLine(new InvoiceLine("QUESO EDAM BOLA MOLDE 1.9KG", 2, new BigDecimal("112.10")));
            invoice.markPaid();
            invoices.save(invoice);
        }

        if (findInvoice(invoices, "INV-2026-0006") == null) {
            Invoice secondInvoice = new Invoice(sourceSecondOrder, "INV-2026-0006");
            secondInvoice.addLine(new InvoiceLine("QUESO GOUDA COMINO MOLDE 4.5KG", 4, new BigDecimal("283.50")));
            secondInvoice.addLine(new InvoiceLine("QUESO MANCHEGO DOP 6 MESES MOLDE 3KG", 1, new BigDecimal("372.00")));
            invoices.save(secondInvoice);
        }

        if (payments.findAll().isEmpty()) {
            Invoice sourceInvoice = invoices.findAll().stream()
                .filter(invoice -> "INV-2026-0001".equals(invoice.getInvoiceNumber()))
                .findFirst()
                .orElseThrow();
            Invoice secondInvoice = invoices.findAll().stream()
                .filter(invoice -> "INV-2026-0006".equals(invoice.getInvoiceNumber()))
                .findFirst()
                .orElse(sourceInvoice);
            payments.save(new Payment(sourceInvoice, new BigDecimal("2294.20"), "Corporate card"));
            payments.save(new Payment(secondInvoice, new BigDecimal("1506.00"), "Bank transfer"));
        }
    }

    private static void seedPurchaseRequests(ProductRepositoryPort products, PurchaseRequestRepositoryPort requests, Product fallbackProduct) {
        if (!requests.existsByCode("PR-2026-0001")) {
            PurchaseRequest request = new PurchaseRequest(1L, "CLI-001", "PR-2026-0001", "buyer_portal", "submitted", "high",
                LocalDate.parse("2026-06-13"), "ADDR-001", "Callao", "Callao", "Callao",
                "Weekend chilled product demand", "bank_transfer", null,
                "Prioritize chilled products for weekend demand.", "");
            request.addItem(new PurchaseRequestLine(1L, products.findBySku("PROD-0004").orElse(fallbackProduct), 3, "box", new BigDecimal("22.50"), ""));
            request.addItem(new PurchaseRequestLine(1L, products.findBySku("PROD-0013").orElse(fallbackProduct), 2, "box", new BigDecimal("3.80"), ""));
            requests.save(request);
        }

        if (!requests.existsByCode("REQ-2026-0004")) {
            PurchaseRequest request = new PurchaseRequest(1L, "CLI-001", "REQ-2026-0004", "buyer_portal", "submitted", "normal",
                LocalDate.parse("2026-06-13"), "ADDR-001", "Callao", "Callao", "Callao",
                "Buyer portal quick request", "bank_transfer", null, "nose", "");
            request.addItem(new PurchaseRequestLine(1L, products.findBySku("PROD-0001").orElse(fallbackProduct), 1, "UN", new BigDecimal("0.15"), ""));
            requests.save(request);
        }
    }

    private static DispatchOrder seedDispatchOrders(SalesOrderRepositoryPort orders, DispatchOrderRepositoryPort dispatchOrders) {
        if (dispatchOrders.existsByCode("DSP-BUY-2026-0301")) {
            return dispatchOrders.findByCode("DSP-BUY-2026-0301").orElse(null);
        }
        SalesOrder sourceOrder = orders.findAll().stream().findFirst().orElse(null);
        if (sourceOrder == null) return null;
        DispatchOrder dispatchOrder = new DispatchOrder(1L, sourceOrder.getId(), 1L, "DSP-BUY-2026-0301",
            "ready_for_operations", "Lima Norte cold route", "", OffsetDateTime.parse("2026-06-13T14:00:00-05:00"),
            "14:00-18:00");
        return dispatchOrders.save(dispatchOrder);
    }

    private static void seedOperationalRecords(DispatchOrder dispatchOrder, DispatchEventRepositoryPort dispatchEvents,
                                               ProofOfDeliveryRecordRepositoryPort proofsOfDelivery,
                                               TemperatureLogRepositoryPort temperatureLogs) {
        if (dispatchOrder == null) return;
        if (dispatchEvents.findByDispatchOrderIdOrderByIdAsc(dispatchOrder.getId()).isEmpty()) {
            dispatchEvents.save(new DispatchEvent(dispatchOrder.getTenantId(), dispatchOrder.getId(), "ready_for_operations",
                "Dispatch order ready for cold-chain operations.", true));
        }
        if (proofsOfDelivery.findByDispatchOrderIdOrderByIdAsc(dispatchOrder.getId()).isEmpty()) {
            proofsOfDelivery.save(new ProofOfDeliveryRecord(dispatchOrder.getTenantId(), dispatchOrder.getId(),
                "", null, false, false, "Proof of delivery pending route completion.", "pending"));
        }
        if (temperatureLogs.findByDispatchOrderIdOrderByIdAsc(dispatchOrder.getId()).isEmpty()) {
            temperatureLogs.save(new TemperatureLog(dispatchOrder.getTenantId(), dispatchOrder.getId(), dispatchOrder.getOrderId(),
                new BigDecimal("3.80"), "truck-cabin", "ok", OffsetDateTime.parse("2026-06-13T13:30:00-05:00")));
        }
    }

    private static SalesOrder findOrderForCustomer(SalesOrderRepositoryPort orders, Customer customer) {
        return orders.findAll().stream()
            .filter(order -> order.getCustomer().getId().equals(customer.getId()))
            .findFirst()
            .orElse(null);
    }

    private static SalesOrder findSecondOrderForCustomer(SalesOrderRepositoryPort orders, Customer customer) {
        return orders.findAll().stream()
            .filter(order -> order.getCustomer().getId().equals(customer.getId()))
            .skip(1)
            .findFirst()
            .orElse(null);
    }

    private static Invoice findInvoice(InvoiceRepositoryPort invoices, String invoiceNumber) {
        return invoices.findAll().stream()
            .filter(invoice -> invoiceNumber.equals(invoice.getInvoiceNumber()))
            .findFirst()
            .orElse(null);
    }

    private static void seedBusinessDocuments(SalesOrderRepositoryPort orders,
                                              BusinessDocumentRepositoryPort businessDocuments,
                                              DocumentTypeRepositoryPort documentTypes,
                                              BusinessDocumentContentGenerator contentGenerator) {
        documentType(documentTypes, "factura_xml", "Factura XML");
        DocumentType invoicePdf = documentType(documentTypes, "factura_pdf", "Factura PDF");
        documentType(documentTypes, "guia_pdf", "Guia PDF");
        documentType(documentTypes, "business_document", "Business document");
        if (!businessDocuments.findByTenantIdOrderByIdAsc(1L).isEmpty()) return;
        SalesOrder sourceOrder = orders.findAll().stream().findFirst()
            .flatMap(order -> orders.findWithItemsById(order.getId()))
            .orElse(null);
        if (sourceOrder == null) return;
        var generated = contentGenerator.generate(sourceOrder, invoicePdf.getKey());
        BusinessDocument document = new BusinessDocument(1L, sourceOrder.getId(), sourceOrder.getCustomer().getId(),
            invoicePdf, "Factura F001-000001", generated.fileName(), true, true);
        document.replaceGeneratedContent(invoicePdf, generated.clientAccountId(), "Factura F001-000001",
            generated.fileName(), generated.contentType(), generated.content());
        businessDocuments.save(document);
    }

    private static DocumentType documentType(DocumentTypeRepositoryPort documentTypes, String key, String label) {
        return documentTypes.findByKeyAndActiveTrue(key)
            .orElseGet(() -> documentTypes.save(new DocumentType(key, label)));
    }

    private static PaymentMethodRecord seedPaymentMethods(Customer customer,
                                                          PaymentMethodRecordRepositoryPort paymentMethods) {
        List<PaymentMethodRecord> existing = paymentMethods.findByTenantIdOrderByClientAccountIdAscIsDefaultDesc(1L);
        if (!existing.isEmpty()) return existing.getFirst();
        return paymentMethods.save(new PaymentMethodRecord(1L, customer.getId(), "credit_line",
            "ICISA credit line", true));
    }

    private static void seedPaymentProcesses(SalesOrderRepositoryPort orders, PaymentRepositoryPort payments,
                                             PaymentProcessRecordRepositoryPort paymentProcesses,
                                             PaymentMethodRecord paymentMethod) {
        if (!paymentProcesses.findByTenantIdOrderByIdAsc(1L).isEmpty()) return;
        SalesOrder sourceOrder = orders.findAll().stream().findFirst().orElse(null);
        Payment sourcePayment = payments.findAll().stream().findFirst().orElse(null);
        paymentProcesses.save(new PaymentProcessRecord(1L, sourceOrder == null ? null : sourceOrder.getId(), 1L,
            sourcePayment == null ? null : sourcePayment.getId(), paymentMethod == null ? null : paymentMethod.getId(),
            new BigDecimal("1944.24"),
            new BigDecimal("0.00"), new BigDecimal("120.00"), new BigDecimal("349.96"),
            new BigDecimal("2414.20"), "pending"));
    }

    private static SalesOrder sourceOrder(ProductRepositoryPort products, SalesOrderRepositoryPort orders,
                                          Customer sourceCustomer, Product fallbackProduct) {
        SalesOrder order = new SalesOrder(sourceCustomer);
        order.changeStatus(OrderStatus.PENDING);
        order.addItem(new SalesOrderItem(products.findBySku("PROD-0004").orElse(fallbackProduct), 3, new BigDecimal("690.00")));
        order.addItem(new SalesOrderItem(products.findBySku("PROD-0013").orElse(fallbackProduct), 2, new BigDecimal("112.10")));
        return orders.save(order);
    }

    private static SalesOrder sourceSecondOrder(ProductRepositoryPort products, SalesOrderRepositoryPort orders,
                                                Customer sourceCustomer, Product fallbackProduct) {
        SalesOrder order = new SalesOrder(sourceCustomer);
        order.changeStatus(OrderStatus.PENDING);
        order.addItem(new SalesOrderItem(products.findBySku("PROD-0019").orElse(fallbackProduct), 4, new BigDecimal("283.50")));
        order.addItem(new SalesOrderItem(products.findBySku("PROD-0048").orElse(fallbackProduct), 1, new BigDecimal("372.00")));
        return orders.save(order);
    }

    private static void fillerOrder(ProductRepositoryPort products, SalesOrderRepositoryPort orders, Customer customer,
                                    Product fallbackProduct, String sku, int quantity) {
        SalesOrder order = new SalesOrder(customer);
        order.changeStatus(OrderStatus.CONFIRMED);
        Product product = products.findBySku(sku).orElse(fallbackProduct);
        order.addItem(new SalesOrderItem(product, quantity, product.getUnitPrice()));
        orders.save(order);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CatalogSeedItem(String sku, String name, String description, String category, String brand,
                                   BigDecimal price, String unit, String imageUrl, Integer stock, Integer reserved,
                                   Integer minStock) { }
}
