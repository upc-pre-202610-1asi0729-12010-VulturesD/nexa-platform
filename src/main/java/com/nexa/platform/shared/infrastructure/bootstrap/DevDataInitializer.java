package com.nexa.platform.shared.infrastructure.bootstrap;

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
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile({"local", "test"})
public class DevDataInitializer {
    @Bean
    CommandLineRunner seed(RoleRepository roles, UserAccountRepository users, PasswordEncoder encoder,
                           CategoryRepository categories, ProductRepository products,
                           WarehouseRepository warehouses, InventoryItemRepository inventory,
                           CustomerRepository customers, DeliveryRouteRepository routes) {
        return args -> {
            Role admin = roles.findByName(RoleName.ROLE_ADMIN).orElseGet(() -> roles.save(new Role(RoleName.ROLE_ADMIN)));
            Role operator = roles.findByName(RoleName.ROLE_OPERATOR).orElseGet(() -> roles.save(new Role(RoleName.ROLE_OPERATOR)));
            Role buyer = roles.findByName(RoleName.ROLE_BUYER).orElseGet(() -> roles.save(new Role(RoleName.ROLE_BUYER)));
            if (!users.existsByEmail("admin@nexa.local")) {
                UserAccount account = new UserAccount("Nexa Admin", "admin@nexa.local", encoder.encode("NexaAdmin123"));
                account.addRole(admin);
                users.save(account);
            }
            if (!users.existsByEmail("operator@nexa.local")) {
                UserAccount account = new UserAccount("Nexa Logistics Operator", "operator@nexa.local", encoder.encode("Operator123"));
                account.addRole(operator);
                users.save(account);
            }
            if (!users.existsByEmail("buyer@nexa.local")) {
                UserAccount account = new UserAccount("Nexa Buyer Portal", "buyer@nexa.local", encoder.encode("Buyer123"));
                account.addRole(buyer);
                users.save(account);
            }
            Category charcuterie = category(categories, "Charcuterie", "Imported cured meats for refrigerated B2B distribution");
            Category cheeses = category(categories, "Cheeses", "European and specialty cheeses for cold-chain customers");
            Category butterAndDairy = category(categories, "Butter and Dairy", "Dairy products requiring temperature-controlled handling");

            Product primaryProduct = product(products, "CAV-SAL-MIL-2K5", "Cavour Salame Milano molde 2.5 kg",
                "Italian salame for horeca and specialty retail channels", charcuterie, "Cavour", BigDecimal.valueOf(94.50),
                "piece", "/assets/catalog-items/Cavour_SALAME_MILANO_MOLDE_2_5KG.jpeg", 0, 4, "Keep refrigerated between 0C and 4C");
            product(products, "CAV-SAL-NAP-1K5", "Cavour Salame Napoli molde 1.5 kg", "Napoli-style cured salame for deli counters",
                charcuterie, "Cavour", BigDecimal.valueOf(88.90), "piece", "/assets/catalog-items/Cavour_SALAME_NAPOLI_MOLDE_1_5KG.jpeg", 0, 4, "Keep refrigerated and sealed");
            product(products, "GES-GOU-NAT-4K5", "Gestam Queso Gouda natural molde 4.5 kg", "Natural Gouda cheese for food service operations",
                cheeses, "Gestam", BigDecimal.valueOf(146.00), "piece", "/assets/catalog-items/Gestam_QUESO_GOUDA_NATURAL_MOLDE_4_5KG.jpeg", 2, 6, "Keep refrigerated after opening");
            product(products, "GES-EDA-BOL-1K9", "Gestam Queso Edam bola molde 1.9 kg", "Edam cheese ball format for retail slicing",
                cheeses, "Gestam", BigDecimal.valueOf(119.00), "piece", "/assets/catalog-items/Gestam_QUESO_EDAM_BOLA_MOLDE_1_9KG.jpeg", 2, 6, "Maintain cold-chain during transport");
            product(products, "GRI-DAN-BLU-100", "Green Island Danish Blue 100 g", "Individual blue cheese unit for premium baskets",
                cheeses, "Green Island", BigDecimal.valueOf(22.40), "unit", "/assets/catalog-items/Green_Island_QUESO_DANISH_BLUE_100G.jpeg", 2, 6, "Keep refrigerated");
            product(products, "PAY-RAC-SLI-400", "Paysan Breton Raclette slices 400 g", "Raclette slices for restaurants and retail packs",
                cheeses, "Paysan Breton", BigDecimal.valueOf(37.90), "pack", "/assets/catalog-items/Paysan_Breton_RACLETTE_SLICES_400G.jpeg", 2, 6, "Keep refrigerated");
            product(products, "PAY-BUT-SAL-20U", "Paysan Breton mantequilla con sal 20 x 10 g", "Portioned salted butter for horeca service",
                butterAndDairy, "Paysan Breton", BigDecimal.valueOf(31.60), "case", "/assets/catalog-items/Paysan_Breton_MANTEQUILLA_CON_SAL_20x10G.jpeg", 2, 6, "Avoid temperature abuse");
            product(products, "SAN-MAN-6M-3K", "Sancho Panza Manchego DOP 6 meses molde 3 kg", "Manchego DOP with six-month maturation",
                cheeses, "Sancho Panza", BigDecimal.valueOf(182.00), "piece", "/assets/catalog-items/Sancho_Panza_QUESO_MANCHEGO_DOP_6_MESES_MOLDE_3KG.jpeg", 2, 6, "Keep refrigerated");
            Warehouse warehouse = warehouses.findAll().stream().findFirst().orElseGet(() -> warehouses.save(new Warehouse("Lima Cold Hub", "Av. Industrial 1200, Lima", TemperatureBand.FROZEN)));
            if (inventory.findAll().isEmpty()) inventory.save(new InventoryItem(warehouse, primaryProduct, 120, 30));
            if (customers.findAll().isEmpty()) customers.save(new Customer("Distribuidora Norte SAC", "20601234567", "compras@dnorte.local", "Av. Los Frigorificos 450"));
            if (routes.findAll().isEmpty()) routes.save(new DeliveryRoute("Lima Norte cold route", "Lima Cold Hub", "Los Olivos distribution zone"));
        };
    }

    private static Category category(CategoryRepository categories, String name, String description) {
        return categories.findByName(name).orElseGet(() -> categories.save(new Category(name, description)));
    }

    private static Product product(ProductRepository products, String sku, String name, String description, Category category,
                                   String supplierName, BigDecimal unitPrice, String unit, String imageUrl,
                                   Integer minCelsius, Integer maxCelsius, String handlingNotes) {
        return products.findBySku(sku).orElseGet(() -> products.save(new Product(sku, name, description, category, supplierName,
            unitPrice, unit, imageUrl, new ColdChainRequirement(minCelsius, maxCelsius, handlingNotes))));
    }
}
