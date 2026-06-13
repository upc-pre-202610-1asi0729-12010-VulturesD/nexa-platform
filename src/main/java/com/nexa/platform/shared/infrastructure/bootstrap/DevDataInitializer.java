package com.nexa.platform.shared.infrastructure.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexa.platform.catalog.domain.model.*;
import com.nexa.platform.catalog.infrastructure.persistence.jpa.*;
import com.nexa.platform.iam.domain.model.*;
import com.nexa.platform.iam.infrastructure.persistence.jpa.*;
import com.nexa.platform.logistics.domain.model.DeliveryRoute;
import com.nexa.platform.logistics.infrastructure.persistence.jpa.DeliveryRouteRepository;
import com.nexa.platform.sales.domain.model.Customer;
import com.nexa.platform.sales.infrastructure.persistence.jpa.CustomerRepository;
import com.nexa.platform.warehouse.domain.model.*;
import com.nexa.platform.warehouse.infrastructure.persistence.jpa.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile({"local", "test"})
public class DevDataInitializer {
    private static final String SOURCE_PROFILE_PASSWORD = "NexaDemo2026!";

    @Bean
    CommandLineRunner seed(RoleRepository roles, UserAccountRepository users, PasswordEncoder encoder,
                           CategoryRepository categories, ProductRepository products,
                           WarehouseRepository warehouses, InventoryItemRepository inventory,
                           CustomerRepository customers, DeliveryRouteRepository routes,
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
            user(users, encoder, "Valeria Sanchez", "sales@nexa.com", SOURCE_PROFILE_PASSWORD, sales);
            user(users, encoder, "Roberto Garcia", "logistics@nexa.com", SOURCE_PROFILE_PASSWORD, logistics);
            user(users, encoder, "Joaquin Verde", "warehouse@nexa.com", SOURCE_PROFILE_PASSWORD, warehouseRole);
            user(users, encoder, "Elena Litano", "buyer.demo@nexa.com", SOURCE_PROFILE_PASSWORD, buyer);

            Product primaryProduct = seedCatalog(categories, products, objectMapper);
            Warehouse warehouse = warehouses.findAll().stream().findFirst().orElseGet(() -> warehouses.save(new Warehouse("Lima Cold Hub", "Av. Industrial 1200, Lima", TemperatureBand.FROZEN)));
            if (inventory.findAll().isEmpty()) inventory.save(new InventoryItem(warehouse, primaryProduct, 120, 30));
            if (customers.findAll().isEmpty()) customers.save(new Customer("Distribuidora Norte SAC", "20601234567", "compras@dnorte.local", "Av. Los Frigorificos 450"));
            if (routes.findAll().isEmpty()) routes.save(new DeliveryRoute("Lima Norte cold route", "Lima Cold Hub", "Los Olivos distribution zone"));
        };
    }

    private static Role role(RoleRepository roles, RoleName name) {
        return roles.findByName(name).orElseGet(() -> roles.save(new Role(name)));
    }

    private static void user(UserAccountRepository users, PasswordEncoder encoder, String fullName, String email,
                             String password, Role role) {
        if (users.existsByEmail(email)) return;
        UserAccount account = new UserAccount(fullName, email, encoder.encode(password));
        account.addRole(role);
        users.save(account);
    }

    private static Category category(CategoryRepository categories, String name, String description) {
        return categories.findByName(name).orElseGet(() -> categories.save(new Category(name, description)));
    }

    private static Product seedCatalog(CategoryRepository categories, ProductRepository products, ObjectMapper objectMapper) throws IOException {
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

    private static Product product(ProductRepository products, String sku, String name, String description, Category category,
                                   String supplierName, BigDecimal unitPrice, String unit, String imageUrl,
                                   Integer availableStock, Integer reservedStock, Integer minStock,
                                   Integer minCelsius, Integer maxCelsius, String handlingNotes) {
        return products.findBySku(sku).orElseGet(() -> products.save(new Product(sku, name, description, category, supplierName,
            unitPrice, unit, imageUrl, availableStock, reservedStock, minStock, new ColdChainRequirement(minCelsius, maxCelsius, handlingNotes))));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CatalogSeedItem(String sku, String name, String description, String category, String brand,
                                   BigDecimal price, String unit, String imageUrl, Integer stock, Integer reserved,
                                   Integer minStock) { }
}
