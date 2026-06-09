USE nexa_platform_db;

-- Nexa AV2 local/demo seed data.
-- Idempotent: natural keys + INSERT IGNORE / WHERE NOT EXISTS.
-- No DROP, TRUNCATE, or broad DELETE statements.

-- 1. Categories
INSERT IGNORE INTO categories (name, description, is_active, created_at, updated_at) VALUES
('Vaccines', 'Temperature-controlled vaccine packs for B2B distribution.', 1, NOW(6), NULL),
('Pharmaceuticals', 'Cold-chain pharmaceutical kits and sensitive medical supplies.', 1, NOW(6), NULL),
('Dairy Products', 'Refrigerated dairy goods for commercial buyers.', 1, NOW(6), NULL),
('Frozen Foods', 'Frozen poultry, vegetables, and prepared food boxes.', 1, NOW(6), NULL),
('Fresh Seafood', 'Fresh and frozen seafood for horeca and retail buyers.', 1, NOW(6), NULL),
('Fresh Produce', 'Export-grade fruit and fresh produce crates.', 1, NOW(6), NULL),
('Cheese', 'Imported and refrigerated cheese catalog items.', 1, NOW(6), NULL),
('Charcuterie', 'Refrigerated deli meats and specialty charcuterie.', 1, NOW(6), NULL);

-- 2. Brands
INSERT IGNORE INTO brands (name, description, is_active, created_at, updated_at) VALUES
('Andes Pharma', 'Peruvian cold-chain pharmaceutical distributor.', 1, NOW(6), NULL),
('Nexa Foods', 'Nexa private-label refrigerated and frozen foods.', 1, NOW(6), NULL),
('FreshLogix', 'Cold-chain logistics brand for fresh produce programs.', 1, NOW(6), NULL),
('Pacifico Seafood', 'Seafood importer serving Lima and coastal B2B buyers.', 1, NOW(6), NULL),
('BioCold Labs', 'Temperature-sensitive lab and medical supply producer.', 1, NOW(6), NULL),
('AgroAndes', 'Fresh produce exporter for Peru regional routes.', 1, NOW(6), NULL),
('Agriform', 'Imported cheese brand present in current catalog seed.', 1, NOW(6), NULL),
('Cavour', 'Charcuterie brand present in current catalog seed.', 1, NOW(6), NULL),
('Gestam', 'Cheese brand present in current catalog seed.', 1, NOW(6), NULL),
('Paysan Breton', 'Dairy brand present in current catalog seed.', 1, NOW(6), NULL);

-- 3. Warehouses
INSERT IGNORE INTO warehouses (name, location, minimum_temperature, maximum_temperature, is_active, created_at, updated_at) VALUES
('Nexa Lima Ate Cold Hub', 'Lima - Ate', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Lima South Frozen Hub', 'Lima - Villa El Salvador', -22.00, -18.00, 1, NOW(6), NULL),
('Nexa Callao Import Hub', 'Callao - Ventanilla', -22.00, 8.00, 1, NOW(6), NULL),
('Nexa Arequipa Regional Hub', 'Arequipa - Cerro Colorado', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Trujillo North Hub', 'Trujillo - Moche', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Chiclayo Coastal Hub', 'Chiclayo - Pimentel', -22.00, 8.00, 1, NOW(6), NULL),
('Nexa Piura Fresh Hub', 'Piura - Castilla', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Cusco Pharma Hub', 'Cusco - Wanchaq', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Ica Produce Hub', 'Ica - Parcona', 2.00, 8.00, 1, NOW(6), NULL),
('Nexa Tacna Border Hub', 'Tacna - Pocollay', -22.00, 8.00, 1, NOW(6), NULL);

-- 4. Catalog Items
INSERT INTO catalog_items (catalog_item_id, product_id, item_name, brand_name, category_name, description, image_url, unit_price_amount, unit_price_currency, available_stock, cold_chain_requirement, is_active, created_at, updated_at)
SELECT * FROM (
    SELECT 'AV2-CAT-0001' AS catalog_item_id, 'AV2-PROD-0001' AS product_id, 'Influenza Vaccine Pack' AS item_name, 'Andes Pharma' AS brand_name, 'Vaccines' AS category_name, 'Validated refrigerated vaccine pack for clinic replenishment.' AS description, 'https://nexa.local/assets/catalog/influenza-vaccine-pack.jpg' AS image_url, 185.00 AS unit_price_amount, 'PEN' AS unit_price_currency, 140 AS available_stock, 'Refrigerated' AS cold_chain_requirement, 1 AS is_active, NOW(6) AS created_at, NULL AS updated_at
    UNION ALL SELECT 'AV2-CAT-0002', 'AV2-PROD-0002', 'Insulin Cold Storage Kit', 'BioCold Labs', 'Pharmaceuticals', 'Insulin handling kit with controlled cold-chain packaging.', 'https://nexa.local/assets/catalog/insulin-cold-storage-kit.jpg', 240.00, 'PEN', 95, 'Refrigerated', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0003', 'AV2-PROD-0003', 'Fresh Salmon Box', 'Pacifico Seafood', 'Fresh Seafood', 'Fresh salmon box for premium horeca buyers.', 'https://nexa.local/assets/catalog/fresh-salmon-box.jpg', 165.00, 'PEN', 80, 'Refrigerated', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0004', 'AV2-PROD-0004', 'Frozen Chicken Breast', 'Nexa Foods', 'Frozen Foods', 'Frozen chicken breast carton for regional distributors.', 'https://nexa.local/assets/catalog/frozen-chicken-breast.jpg', 115.00, 'PEN', 170, 'Frozen', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0005', 'AV2-PROD-0005', 'Greek Yogurt Crate', 'Nexa Foods', 'Dairy Products', 'Refrigerated Greek yogurt crate for supermarket replenishment.', 'https://nexa.local/assets/catalog/greek-yogurt-crate.jpg', 68.00, 'PEN', 220, 'Refrigerated', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0006', 'AV2-PROD-0006', 'Blueberry Export Pack', 'AgroAndes', 'Fresh Produce', 'Fresh blueberry pack for export-grade cold-chain routes.', 'https://nexa.local/assets/catalog/blueberry-export-pack.jpg', 92.00, 'PEN', 180, 'Refrigerated', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0007', 'AV2-PROD-0007', 'Shrimp Frozen Bag', 'Pacifico Seafood', 'Fresh Seafood', 'Frozen shrimp bag for food service distribution.', 'https://nexa.local/assets/catalog/shrimp-frozen-bag.jpg', 145.00, 'PEN', 120, 'Frozen', 1, NOW(6), NULL
    UNION ALL SELECT 'AV2-CAT-0008', 'AV2-PROD-0008', 'Pharmaceutical Starter Kit', 'Andes Pharma', 'Pharmaceuticals', 'Starter pharmaceutical kit for clinic onboarding orders.', 'https://nexa.local/assets/catalog/pharmaceutical-starter-kit.jpg', 310.00, 'PEN', 70, 'Refrigerated', 1, NOW(6), NULL
) AS src
WHERE NOT EXISTS (SELECT 1 FROM catalog_items c WHERE c.catalog_item_id = src.catalog_item_id);

-- 5. Inventory Items
INSERT INTO inventory_items (product_id, catalog_item_id, available_quantity, reserved_quantity, warehouse_location, minimum_temperature, maximum_temperature, created_at, updated_at)
SELECT * FROM (
    SELECT 'AV2-PROD-0001' AS product_id, 'AV2-CAT-0001' AS catalog_item_id, 132 AS available_quantity, 8 AS reserved_quantity, 'Lima - Ate' AS warehouse_location, 2.00 AS minimum_temperature, 8.00 AS maximum_temperature, NOW(6) AS created_at, NULL AS updated_at
    UNION ALL SELECT 'AV2-PROD-0002', 'AV2-CAT-0002', 88, 7, 'Cusco - Wanchaq', 2.00, 8.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0003', 'AV2-CAT-0003', 74, 6, 'Callao - Ventanilla', 0.00, 4.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0004', 'AV2-CAT-0004', 156, 14, 'Lima - Villa El Salvador', -22.00, -18.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0005', 'AV2-CAT-0005', 204, 16, 'Trujillo - Moche', 2.00, 8.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0006', 'AV2-CAT-0006', 168, 12, 'Ica - Parcona', 2.00, 8.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0007', 'AV2-CAT-0007', 112, 8, 'Chiclayo - Pimentel', -22.00, -18.00, NOW(6), NULL
    UNION ALL SELECT 'AV2-PROD-0008', 'AV2-CAT-0008', 64, 6, 'Arequipa - Cerro Colorado', 2.00, 8.00, NOW(6), NULL
) AS src
WHERE NOT EXISTS (SELECT 1 FROM inventory_items i WHERE i.catalog_item_id = src.catalog_item_id);

-- 6. Orders
INSERT IGNORE INTO orders (order_number, customer_id, status, total_amount, total_currency, payment_confirmation, inventory_reservation, rejection_reason, confirmed_at, created_at, updated_at) VALUES
('AV2-ORD-0001', 'CLI-001', 'Pending', 610.00, 'PEN', NULL, NULL, NULL, NULL, NOW(6), NULL),
('AV2-ORD-0002', 'CLI-002', 'Confirmed', 1075.00, 'PEN', 'BCP-OP-AV2-0002', 'RES-AV2-0002', NULL, NOW(6), NOW(6), NULL),
('AV2-ORD-0003', 'CLI-003', 'Paid', 868.00, 'PEN', 'YAPE-AV2-0003', 'RES-AV2-0003', NULL, NOW(6), NOW(6), NULL),
('AV2-ORD-0004', 'CLI-004', 'Confirmed', 824.00, 'PEN', 'BBVA-AV2-0004', 'RES-AV2-0004', NULL, NOW(6), NOW(6), NULL),
('AV2-ORD-0005', 'CLI-005', 'Pending', 1208.00, 'PEN', NULL, NULL, NULL, NULL, NOW(6), NULL);

-- 7. Order Items
INSERT INTO order_items (order_id, product_id, catalog_item_id, item_name, quantity, unit_price_amount, unit_price_currency, subtotal_amount, subtotal_currency)
SELECT o.id, src.product_id, src.catalog_item_id, src.item_name, src.quantity, src.unit_price_amount, 'PEN', src.subtotal_amount, 'PEN'
FROM orders o
JOIN (
    SELECT 'AV2-ORD-0001' order_number, 'AV2-PROD-0001' product_id, 'AV2-CAT-0001' catalog_item_id, 'Influenza Vaccine Pack' item_name, 2 quantity, 185.00 unit_price_amount, 370.00 subtotal_amount
    UNION ALL SELECT 'AV2-ORD-0001', 'AV2-PROD-0002', 'AV2-CAT-0002', 'Insulin Cold Storage Kit', 1, 240.00, 240.00
    UNION ALL SELECT 'AV2-ORD-0002', 'AV2-PROD-0003', 'AV2-CAT-0003', 'Fresh Salmon Box', 3, 165.00, 495.00
    UNION ALL SELECT 'AV2-ORD-0002', 'AV2-PROD-0007', 'AV2-CAT-0007', 'Shrimp Frozen Bag', 4, 145.00, 580.00
    UNION ALL SELECT 'AV2-ORD-0003', 'AV2-PROD-0005', 'AV2-CAT-0005', 'Greek Yogurt Crate', 6, 68.00, 408.00
    UNION ALL SELECT 'AV2-ORD-0003', 'AV2-PROD-0006', 'AV2-CAT-0006', 'Blueberry Export Pack', 5, 92.00, 460.00
    UNION ALL SELECT 'AV2-ORD-0004', 'AV2-PROD-0004', 'AV2-CAT-0004', 'Frozen Chicken Breast', 4, 115.00, 460.00
    UNION ALL SELECT 'AV2-ORD-0004', 'AV2-PROD-0008', 'AV2-CAT-0008', 'Pharmaceutical Starter Kit', 1, 310.00, 310.00
    UNION ALL SELECT 'AV2-ORD-0005', 'AV2-PROD-0005', 'AV2-CAT-0005', 'Greek Yogurt Crate', 10, 68.00, 680.00
    UNION ALL SELECT 'AV2-ORD-0005', 'AV2-PROD-0006', 'AV2-CAT-0006', 'Blueberry Export Pack', 5, 92.00, 460.00
) src ON src.order_number = o.order_number
WHERE NOT EXISTS (
    SELECT 1 FROM order_items oi WHERE oi.order_id = o.id AND oi.catalog_item_id = src.catalog_item_id
);

-- 8. Shipments
INSERT INTO shipments (shipment_code, order_id, scheduled_at, delivered_at, status, last_temperature_celsius, last_temperature_recorded_at, created_at, updated_at)
SELECT src.shipment_code, o.id, src.scheduled_at, src.delivered_at, src.status, src.last_temperature_celsius, src.last_temperature_recorded_at, NOW(6), NULL
FROM orders o
JOIN (
    SELECT 'AV2-SHP-0001' shipment_code, 'AV2-ORD-0001' order_number, DATE_ADD(NOW(6), INTERVAL 1 DAY) scheduled_at, NULL delivered_at, 'Scheduled' status, 4.20 last_temperature_celsius, NOW(6) last_temperature_recorded_at
    UNION ALL SELECT 'AV2-SHP-0002', 'AV2-ORD-0002', DATE_SUB(NOW(6), INTERVAL 2 DAY), DATE_SUB(NOW(6), INTERVAL 1 DAY), 'Delivered', -18.70, DATE_SUB(NOW(6), INTERVAL 1 DAY)
    UNION ALL SELECT 'AV2-SHP-0003', 'AV2-ORD-0003', DATE_SUB(NOW(6), INTERVAL 1 DAY), DATE_SUB(NOW(6), INTERVAL 6 HOUR), 'Delivered', 5.10, DATE_SUB(NOW(6), INTERVAL 6 HOUR)
    UNION ALL SELECT 'AV2-SHP-0004', 'AV2-ORD-0004', DATE_ADD(NOW(6), INTERVAL 2 DAY), NULL, 'Scheduled', -19.20, NOW(6)
    UNION ALL SELECT 'AV2-SHP-0005', 'AV2-ORD-0005', DATE_ADD(NOW(6), INTERVAL 3 DAY), NULL, 'Scheduled', 6.00, NOW(6)
    UNION ALL SELECT 'AV2-SHP-0006', 'ORD-2026-0010', DATE_ADD(NOW(6), INTERVAL 5 DAY), NULL, 'Scheduled', 3.90, NOW(6)
) src ON src.order_number = o.order_number
WHERE NOT EXISTS (SELECT 1 FROM shipments s WHERE s.shipment_code = src.shipment_code);

-- 9. Invoices
INSERT INTO invoices (invoice_number, order_id, amount, currency, payment_status, paid_at, created_at, updated_at)
SELECT src.invoice_number, o.id, src.amount, 'PEN', src.payment_status, src.paid_at, NOW(6), NULL
FROM orders o
JOIN (
    SELECT 'AV2-INV-0001' invoice_number, 'AV2-ORD-0001' order_number, 610.00 amount, 'Pending' payment_status, NULL paid_at
    UNION ALL SELECT 'AV2-INV-0002', 'AV2-ORD-0002', 1075.00, 'Paid', DATE_SUB(NOW(6), INTERVAL 1 DAY)
    UNION ALL SELECT 'AV2-INV-0003', 'AV2-ORD-0003', 868.00, 'Paid', NOW(6)
    UNION ALL SELECT 'AV2-INV-0004', 'AV2-ORD-0004', 824.00, 'Pending', NULL
    UNION ALL SELECT 'AV2-INV-0005', 'AV2-ORD-0005', 1208.00, 'Pending', NULL
    UNION ALL SELECT 'AV2-INV-0006', 'ORD-2026-0010', 2062.38, 'Pending', NULL
) src ON src.order_number = o.order_number
WHERE NOT EXISTS (SELECT 1 FROM invoices i WHERE i.invoice_number = src.invoice_number);

-- 10. Payments
INSERT INTO payments (invoice_id, amount, currency, reference_code, status, created_at, updated_at)
SELECT i.id, src.amount, 'PEN', src.reference_code, src.status, NOW(6), NULL
FROM invoices i
JOIN (
    SELECT 'AV2-INV-0002' invoice_number, 1075.00 amount, 'AV2-PAY-0001' reference_code, 'Paid' status
    UNION ALL SELECT 'AV2-INV-0003', 868.00, 'AV2-PAY-0002', 'Paid'
    UNION ALL SELECT 'AV2-INV-0004', 412.00, 'AV2-PAY-0003', 'Pending'
    UNION ALL SELECT 'AV2-INV-0005', 604.00, 'AV2-PAY-0004', 'Pending'
    UNION ALL SELECT 'AV2-INV-0006', 1031.19, 'AV2-PAY-0005', 'Pending'
) src ON src.invoice_number = i.invoice_number
WHERE NOT EXISTS (SELECT 1 FROM payments p WHERE p.reference_code = src.reference_code);

-- 11. Verification Queries
SELECT COUNT(*) AS users_count FROM users;
SELECT COUNT(*) AS categories_count FROM categories;
SELECT COUNT(*) AS brands_count FROM brands;
SELECT COUNT(*) AS warehouses_count FROM warehouses;
SELECT COUNT(*) AS catalog_items_count FROM catalog_items;
SELECT COUNT(*) AS inventory_items_count FROM inventory_items;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS order_items_count FROM order_items;
SELECT COUNT(*) AS shipments_count FROM shipments;
SELECT COUNT(*) AS invoices_count FROM invoices;
SELECT COUNT(*) AS payments_count FROM payments;
