package com.nexa.platform.shared.infrastructure.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.domain.model.repositories.*;
import com.nexa.platform.iam.domain.model.*;
import com.nexa.platform.iam.domain.model.repositories.*;
import com.nexa.platform.invoicing.domain.model.*;
import com.nexa.platform.invoicing.domain.model.repositories.*;
import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import com.nexa.platform.logistics.domain.model.repositories.DeliveryRouteRepositoryPort;
import com.nexa.platform.sales.domain.model.*;
import com.nexa.platform.sales.domain.model.repositories.*;
import com.nexa.platform.warehouse.domain.model.*;
import com.nexa.platform.warehouse.domain.model.repositories.*;
import java.io.IOException;
import java.math.BigDecimal;
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

    @Bean
    CommandLineRunner seed(RoleRepositoryPort roles, UserAccountRepositoryPort users, PasswordEncoder encoder,
                           CategoryRepositoryPort categories, ProductRepositoryPort products,
                           WarehouseRepositoryPort warehouses, InventoryItemRepositoryPort inventory,
                           CustomerRepositoryPort customers, DeliveryRouteRepositoryPort routes,
                           SalesOrderRepositoryPort orders, InvoiceRepositoryPort invoices, PaymentRepositoryPort payments,
                           PromotionRepository promotions,
                           ObjectMapper objectMapper) {
        return args -> {
            Role admin = role(roles, RoleName.ROLE_ADMIN);
            Role operator = role(roles, RoleName.ROLE_OPERATOR);
            Role sales = role(roles, RoleName.ROLE_SALES);
            Role logistics = role(roles, RoleName.ROLE_LOGISTICS);
            Role warehouseRole = role(roles, RoleName.ROLE_WAREHOUSE);
            Role buyer = role(roles, RoleName.ROLE_BUYER);

            user(users, encoder, "Nexa Admin", "admin@nexa.local", "NexaAdmin123", admin);
            user(users, encoder, "Nexa Logistics Operator", "operator@nexa.local", "Operator123", operator);
            user(users, encoder, "Nexa Buyer Portal", "buyer@nexa.local", "Buyer123", buyer);
            user(users, encoder, "Valeria Sanchez", "sales@nexa.com", WORKSPACE_PROFILE_PASSWORD, sales);
            user(users, encoder, "Roberto Garcia", "logistics@nexa.com", WORKSPACE_PROFILE_PASSWORD, logistics);
            user(users, encoder, "Joaquin Verde", "warehouse@nexa.com", WORKSPACE_PROFILE_PASSWORD, warehouseRole);
            user(users, encoder, "Elena Litano", "buyer@nexa.com", WORKSPACE_PROFILE_PASSWORD, buyer);

            Product primaryProduct = seedCatalog(categories, products, objectMapper);
            Warehouse warehouse = warehouses.findAll().stream().findFirst().orElseGet(() -> warehouses.save(new Warehouse("Lima Cold Hub", "Av. Industrial 1200, Lima", TemperatureBand.FROZEN)));
            if (inventory.findAll().isEmpty()) inventory.save(new InventoryItem(warehouse, primaryProduct, 120, 30));
            Customer sourceCustomer = sourceCustomer(customers);
            Customer secondaryCustomer = secondaryCustomer(customers);
            seedSourceBuyerOrderAndInvoice(products, orders, invoices, payments, sourceCustomer, secondaryCustomer, primaryProduct);
            if (routes.findAll().isEmpty()) routes.save(new DeliveryRoute("Lima Norte cold route", "Lima Cold Hub", "Los Olivos distribution zone"));

            if (promotions.findAll().isEmpty()) {
                promotions.save(new Promotion("PROMO-COLD-001", "Chilled cheese rotation", 
                    "Commercial bundle for selected cheese lines with short route windows.", 
                    "8% commercial adjustment", "buyer_portal", "active", "Subject to stock and credit validation.",
                    List.of("PROD-0013", "PROD-0014")));
                promotions.save(new Promotion("PROMO-COLD-002", "Food service charcuterie pack", 
                    "Bundle support for hotels and restaurants with recurring weekly demand.", 
                    "Tiered price", "client_specific", "active", "Available for approved B2B buyers.",
                    List.of("PROD-0004", "PROD-0005")));
                promotions.save(new Promotion("PROMO-COLD-003", "Frozen seafood planning", 
                    "Planning reminder for frozen inventory reservations before weekend dispatch.", 
                    "Route priority", "internal", "scheduled", "Operations capacity review required.",
                    List.of("PROD-0042")));
            }
        };
    }

    private static Role role(RoleRepositoryPort roles, RoleName name) {
        return roles.findByName(name).orElseGet(() -> roles.save(new Role(name)));
    }

    private static void user(UserAccountRepositoryPort users, PasswordEncoder encoder, String fullName, String email,
                             String password, Role role) {
        if (users.existsByEmail(email)) return;
        UserAccount account = new UserAccount(fullName, email, encoder.encode(password));
        account.addRole(role);
        users.save(account);
    }

    private static Category category(CategoryRepositoryPort categories, String name, String description) {
        return categories.findByName(name).orElseGet(() -> categories.save(new Category(name, description)));
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
            .orElseGet(() -> customers.save(new Customer("Importaciones y Comercio Internacional S.A.", "20600000001",
                "compras@icisa.pe", "Av. Argentina 2450, Callao")));
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
