package com.nexa.platform.shared.infrastructure.persistence.jpa;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration("databaseMigrationConfig")
public class DatabaseMigrationConfig {

    @Autowired
    public void migrateDatabase(DataSource dataSource) {
        System.out.println("[Migration] Starting pre-JPA database migration checks...");
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement()) {
                String dbProduct = conn.getMetaData().getDatabaseProductName();
                System.out.println("[Migration] Database Product detected: " + dbProduct);
                if ("PostgreSQL".equalsIgnoreCase(dbProduct)) {
                    // Category & Brand & DocumentType active column additions
                    runUpdate(stmt, "ALTER TABLE catalog_categories ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;");
                    runUpdate(stmt, "ALTER TABLE catalog_brands ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;");
                    runUpdate(stmt, "ALTER TABLE catalog_products ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;");
                    runUpdate(stmt, "ALTER TABLE shared_document_types ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;");
                    runUpdate(stmt, "UPDATE catalog_categories SET active = true WHERE active IS NULL;");
                    runUpdate(stmt, "UPDATE catalog_brands SET active = true WHERE active IS NULL;");
                    runUpdate(stmt, "UPDATE catalog_products SET active = true WHERE active IS NULL;");
                    runUpdate(stmt, "UPDATE shared_document_types SET active = true WHERE active IS NULL;");

                    // Promotion compatibility for existing Render PostgreSQL databases
                    runUpdate(stmt, "ALTER TABLE IF EXISTS catalog_promotions ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "UPDATE catalog_promotions SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS catalog_promotions ALTER COLUMN tenant_id SET NOT NULL;");

                    // Invoicing compatibility for existing Render PostgreSQL databases
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_invoices ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_invoices ADD COLUMN IF NOT EXISTS currency varchar(3) DEFAULT 'PEN';");
                    runUpdate(stmt, "UPDATE invoicing_invoices SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE invoicing_invoices SET currency = 'PEN' WHERE currency IS NULL OR btrim(currency) = '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_invoices ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_invoices ALTER COLUMN currency SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ADD COLUMN IF NOT EXISTS currency varchar(8) DEFAULT 'PEN';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ADD COLUMN IF NOT EXISTS reference_code varchar(60);");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ADD COLUMN IF NOT EXISTS status varchar(30) DEFAULT 'PENDING';");
                    runUpdate(stmt, "UPDATE invoicing_payments SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE invoicing_payments SET currency = 'PEN' WHERE currency IS NULL OR btrim(currency) = '';");
                    runUpdate(stmt, "UPDATE invoicing_payments SET reference_code = 'PAY-MIG-' || id WHERE reference_code IS NULL OR btrim(reference_code) = '';");
                    runUpdate(stmt, "UPDATE invoicing_payments SET status = 'PENDING' WHERE status IS NULL OR btrim(status) = '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ALTER COLUMN currency SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ALTER COLUMN reference_code SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS invoicing_payments ALTER COLUMN status SET NOT NULL;");

                    // Sales customer compatibility for existing Render PostgreSQL databases
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS code varchar(40);");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS commercial_name varchar(140);");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS segment varchar(60) DEFAULT 'B2B';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS contact varchar(120) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS phone varchar(40) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS district varchar(80) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS province varchar(80) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS delivery_reference varchar(180) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS document_profile varchar(80) DEFAULT 'ruc_factura_xml_pdf_guia';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS payment_condition varchar(40) DEFAULT 'credit_15';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS monthly_credit_limit numeric(14,2) DEFAULT 0;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS monthly_credit_used numeric(14,2) DEFAULT 0;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS monthly_credit_status varchar(40) DEFAULT 'ok';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS delivery_preference varchar(120) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS portal_access boolean DEFAULT true;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS seller_workspace_email varchar(160) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ADD COLUMN IF NOT EXISTS status varchar(40) DEFAULT 'active';");
                    runUpdate(stmt, "UPDATE sales_customers SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET code = 'CLI-' || id WHERE code IS NULL OR btrim(code) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET commercial_name = business_name WHERE commercial_name IS NULL OR btrim(commercial_name) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET segment = 'B2B' WHERE segment IS NULL OR btrim(segment) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET contact = '' WHERE contact IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET phone = '' WHERE phone IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET district = '' WHERE district IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET province = '' WHERE province IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET delivery_reference = '' WHERE delivery_reference IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET document_profile = 'ruc_factura_xml_pdf_guia' WHERE document_profile IS NULL OR btrim(document_profile) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET payment_condition = 'credit_15' WHERE payment_condition IS NULL OR btrim(payment_condition) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET monthly_credit_limit = 0 WHERE monthly_credit_limit IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET monthly_credit_used = 0 WHERE monthly_credit_used IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET monthly_credit_status = 'ok' WHERE monthly_credit_status IS NULL OR btrim(monthly_credit_status) = '';");
                    runUpdate(stmt, "UPDATE sales_customers SET delivery_preference = '' WHERE delivery_preference IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET portal_access = true WHERE portal_access IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET seller_workspace_email = '' WHERE seller_workspace_email IS NULL;");
                    runUpdate(stmt, "UPDATE sales_customers SET status = 'active' WHERE status IS NULL OR btrim(status) = '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN code SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN commercial_name SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN segment SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN contact SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN phone SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN district SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN province SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN delivery_reference SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN document_profile SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN payment_condition SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN monthly_credit_limit SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN monthly_credit_used SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN monthly_credit_status SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN delivery_preference SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN portal_access SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN seller_workspace_email SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_customers ALTER COLUMN status SET NOT NULL;");

                    // Sales order compatibility for existing Render PostgreSQL databases
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ADD COLUMN IF NOT EXISTS priority varchar(20) DEFAULT 'normal';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ADD COLUMN IF NOT EXISTS notes varchar(800) DEFAULT '';");
                    runUpdate(stmt, "UPDATE sales_orders SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE sales_orders SET priority = 'normal' WHERE priority IS NULL OR btrim(priority) = '';");
                    runUpdate(stmt, "UPDATE sales_orders SET notes = '' WHERE notes IS NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ALTER COLUMN priority SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS sales_orders ALTER COLUMN notes SET NOT NULL;");

                    // Tenant Management tenant_id column additions
                    runUpdate(stmt, "ALTER TABLE tenant_management_workspaces ADD COLUMN IF NOT EXISTS tenant_id bigint;");
                    runUpdate(stmt, "ALTER TABLE tenant_management_members ADD COLUMN IF NOT EXISTS tenant_id bigint;");
                    runUpdate(stmt, "ALTER TABLE tenant_management_subscriptions ADD COLUMN IF NOT EXISTS tenant_id bigint;");
                    runUpdate(stmt, "ALTER TABLE tenant_management_user_workspace_memberships ADD COLUMN IF NOT EXISTS tenant_id bigint;");
                    runUpdate(stmt, "UPDATE tenant_management_workspaces SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE tenant_management_members SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE tenant_management_subscriptions SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE tenant_management_user_workspace_memberships SET tenant_id = 1 WHERE tenant_id IS NULL;");

                    // Warehouse tenant_id column additions
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_locations ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ADD COLUMN IF NOT EXISTS quantity_reserved integer DEFAULT 0;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ADD COLUMN IF NOT EXISTS reorder_point integer DEFAULT 0;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservations ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservations ADD COLUMN IF NOT EXISTS status varchar(40) DEFAULT 'reserved';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservation_records ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ADD COLUMN IF NOT EXISTS reserved_quantity integer DEFAULT 0;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ADD COLUMN IF NOT EXISTS entry_date date DEFAULT CURRENT_DATE;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ADD COLUMN IF NOT EXISTS zone varchar(80) DEFAULT '';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ADD COLUMN IF NOT EXISTS status varchar(40) DEFAULT 'active';");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_movements ADD COLUMN IF NOT EXISTS tenant_id bigint DEFAULT 1;");
                    runUpdate(stmt, "UPDATE warehouse_locations SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_inventory_items SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_inventory_items SET quantity_reserved = 0 WHERE quantity_reserved IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_inventory_items SET reorder_point = 0 WHERE reorder_point IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_inventory_reservations SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_inventory_reservations SET status = 'reserved' WHERE status IS NULL OR btrim(status) = '';");
                    runUpdate(stmt, "UPDATE warehouse_inventory_reservation_records SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_stock_batches SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_stock_batches SET reserved_quantity = 0 WHERE reserved_quantity IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_stock_batches SET entry_date = CURRENT_DATE WHERE entry_date IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_stock_batches SET zone = '' WHERE zone IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_stock_batches SET status = 'active' WHERE status IS NULL OR btrim(status) = '';");
                    runUpdate(stmt, "UPDATE warehouse_inventory_movements SET tenant_id = 1 WHERE tenant_id IS NULL;");
                    runUpdate(stmt, "UPDATE warehouse_locations SET name = 'Av. Guillermo Dansey 2026, Cercado de Lima', address = 'Av. Guillermo Dansey 2026, Cercado de Lima' WHERE name IN ('Lima Cold Hub', 'ICISA Lima Cold Hub') OR address IN ('Av. Industrial 1200, Lima', 'Lima Cold Hub', 'ICISA Lima Cold Hub');");
                    runUpdate(stmt, "UPDATE logistics_delivery_routes SET origin = 'Av. Guillermo Dansey 2026, Cercado de Lima' WHERE origin IN ('Lima Cold Hub', 'ICISA Lima Cold Hub', 'Av. Industrial 1200, Lima');");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_locations ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ALTER COLUMN quantity_reserved SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_items ALTER COLUMN reorder_point SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservations ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservations ALTER COLUMN status SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_reservation_records ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ALTER COLUMN tenant_id SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ALTER COLUMN reserved_quantity SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ALTER COLUMN entry_date SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ALTER COLUMN zone SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_stock_batches ALTER COLUMN status SET NOT NULL;");
                    runUpdate(stmt, "ALTER TABLE IF EXISTS warehouse_inventory_movements ALTER COLUMN tenant_id SET NOT NULL;");
                }
            }
        } catch (Exception e) {
            System.err.println("[Migration] Failed to connect to DataSource for migration: " + e.getMessage());
        }
        System.out.println("[Migration] Pre-JPA database migration checks completed.");
    }

    private void runUpdate(Statement stmt, String sql) {
        try {
            System.out.println("[Migration] Executing: " + sql);
            stmt.execute(sql);
            System.out.println("[Migration] Success: " + sql);
        } catch (Exception e) {
            System.err.println("[Migration] Warning (ignored): " + sql + " -> " + e.getMessage());
        }
    }

    @Component
    public static class EntityManagerDependencyPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            if (beanFactory.containsBeanDefinition("entityManagerFactory")) {
                BeanDefinition bd = beanFactory.getBeanDefinition("entityManagerFactory");
                String[] dependsOn = bd.getDependsOn();
                List<String> list = (dependsOn == null) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(dependsOn));
                if (!list.contains("databaseMigrationConfig")) {
                    list.add("databaseMigrationConfig");
                    bd.setDependsOn(list.toArray(new String[0]));
                    System.out.println("[Migration] Configured entityManagerFactory to depend on databaseMigrationConfig");
                }
            }
        }
    }
}
