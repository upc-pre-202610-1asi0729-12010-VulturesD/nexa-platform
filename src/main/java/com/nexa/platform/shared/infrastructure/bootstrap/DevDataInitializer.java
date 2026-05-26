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
            roles.findByName(RoleName.ROLE_OPERATOR).orElseGet(() -> roles.save(new Role(RoleName.ROLE_OPERATOR)));
            if (!users.existsByEmail("admin@nexa.local")) {
                UserAccount account = new UserAccount("Nexa Admin", "admin@nexa.local", encoder.encode("NexaAdmin123"));
                account.addRole(admin);
                users.save(account);
            }
            Category seafood = categories.findByName("Frozen seafood").orElseGet(() -> categories.save(new Category("Frozen seafood", "Imported seafood requiring cold-chain handling")));
            Product salmon = products.findBySku("FSH-SAL-001").orElseGet(() -> products.save(new Product("FSH-SAL-001", "Atlantic salmon box", "Premium frozen salmon for B2B customers", seafood, "Andes Cold Imports", BigDecimal.valueOf(180.00), new ColdChainRequirement(-18, -12, "Keep sealed and monitored"))));
            Warehouse warehouse = warehouses.findAll().stream().findFirst().orElseGet(() -> warehouses.save(new Warehouse("Lima Cold Hub", "Av. Industrial 1200, Lima", TemperatureBand.FROZEN)));
            if (inventory.findAll().isEmpty()) inventory.save(new InventoryItem(warehouse, salmon, 120, 30));
            if (customers.findAll().isEmpty()) customers.save(new Customer("Distribuidora Norte SAC", "20601234567", "compras@dnorte.local", "Av. Los Frigorificos 450"));
            if (routes.findAll().isEmpty()) routes.save(new DeliveryRoute("Lima Norte cold route", "Lima Cold Hub", "Los Olivos distribution zone"));
        };
    }
}
