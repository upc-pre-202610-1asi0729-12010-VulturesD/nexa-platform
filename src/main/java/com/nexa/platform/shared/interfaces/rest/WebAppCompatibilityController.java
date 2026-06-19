package com.nexa.platform.shared.interfaces.rest;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class WebAppCompatibilityController {
    @GetMapping("/alerts")
    public List<Map<String, Object>> alerts() {
        return List.of(
            item("id", "ALT-001", "type", "danger", "priority", 1, "title", "Credit limit reached - FreshMart Bodegas",
                "desc", "CLI-005 reached the approved limit. Order ORD-2026-0407 is blocked for review.", "action", "View client", "screen", "clients"),
            item("id", "ALT-002", "type", "warning", "priority", 2, "title", "Expiring stock - cold room",
                "desc", "LOT-2026-003 expires soon and requires FEFO dispatch priority.", "action", "View stock", "screen", "inventory"),
            item("id", "ALT-003", "type", "info", "priority", 3, "title", "Pending evidence",
                "desc", "DSP-2026-0088 requires delivery evidence upload.", "action", "View dispatch", "screen", "dispatch")
        );
    }

    @GetMapping("/activity-log")
    public List<Map<String, Object>> activityLog() {
        return List.of(
            item("id", "ACT-001", "time", "09:14", "text", "Order ORD-2026-0001 confirmed - ICISA", "type", "success"),
            item("id", "ACT-002", "time", "08:55", "text", "Catalog synchronized with 50 cold-chain products", "type", "info"),
            item("id", "ACT-003", "time", "08:40", "text", "DSP-2026-0088 left for route", "type", "info"),
            item("id", "ACT-004", "time", "08:20", "text", "Purchase request PR-2026-0001 received", "type", "warning")
        );
    }

    @GetMapping("/clients")
    public List<Map<String, Object>> clients() {
        return List.of(
            item("id", "CLI-001", "name", "Importaciones y Comercio Internacional S.A.", "ruc", "20600000001",
                "contact", "Elena Litano", "phone", "+51 987 654 321", "condition", "30 days net",
                "status", "active", "type", "Importer", "address", "Av. Argentina 2450, Callao",
                "lastOrder", "2026-06-10", "creditLimit", 45000, "creditUsed", 2294.20),
            item("id", "CLI-002", "name", "Hotel Costa del Sol", "ruc", "20502345678",
                "contact", "Carlos Huaman", "phone", "+51 976 543 210", "condition", "45 days net",
                "status", "active", "type", "Hotel", "address", "Via Expresa 1451, San Isidro",
                "lastOrder", "2026-06-09", "creditLimit", 30000, "creditUsed", 12000)
        );
    }

    @GetMapping("/dispatches")
    public List<Map<String, Object>> dispatches() {
        return dispatchRows();
    }

    @GetMapping("/dispatches/{id}")
    public Map<String, Object> dispatch(@PathVariable String id) {
        return dispatchRows().stream()
            .filter(item -> id.equals(item.get("id")))
            .findFirst()
            .orElseGet(() -> item("id", id, "status", "pending", "checklist", List.of()));
    }

    @PatchMapping("/dispatches/{id}")
    public Map<String, Object> patchDispatch(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Map<String, Object> row = new LinkedHashMap<>(dispatch(id));
        row.putAll(payload);
        return row;
    }

    @GetMapping("/inventory-lots")
    public List<Map<String, Object>> inventoryLots() {
        return List.of(
            item("id", "LOT-2026-003", "productId", "PROD-0004", "qty", 11, "reserved", 4,
                "expiry", "2026-07-05", "entryDate", "2026-06-01", "status", "expiring", "warehouse", "Lima Cold Hub", "zone", "CF-2"),
            item("id", "LOT-2026-014", "productId", "PROD-0001", "qty", 42, "reserved", 12,
                "expiry", "2027-03-20", "entryDate", "2026-03-07", "status", "ok", "warehouse", "Lima Cold Hub", "zone", "CF-1"),
            item("id", "LOT-2026-015", "productId", "PROD-0013", "qty", 58, "reserved", 10,
                "expiry", "2027-06-06", "entryDate", "2026-03-25", "status", "ok", "warehouse", "Lima Cold Hub", "zone", "CF-1")
        );
    }

    private List<Map<String, Object>> dispatchRows() {
        return List.of(
            item("id", "DSP-2026-0089", "orderId", "ORD-2026-0001", "clientId", "CLI-001", "status", "ready",
                "driver", "Juan Ccoya Mamani", "vehicle", "B8K-421 - Canter 3T Ref.", "dest", "Av. Argentina 2450, Callao",
                "tempExit", null, "evidenceRequired", false, "evidenceDone", false,
                "checklist", List.of("Load verified", "Temperature 2C to 8C OK", "Documents ready")),
            item("id", "DSP-2026-0088", "orderId", "ORD-2026-0001", "clientId", "CLI-001", "status", "in_transit",
                "driver", "Pedro Quispe Condori", "vehicle", "C2M-889 - Hyundai H1 Ref.", "dest", "Av. Argentina 2450, Callao",
                "tempExit", 4.2, "evidenceRequired", true, "evidenceDone", false,
                "checklist", List.of("Load verified", "Temperature telemetry OK", "Documents ready"))
        );
    }

    private Map<String, Object> item(Object... values) {
        Map<String, Object> item = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) {
            item.put(String.valueOf(values[i]), values[i + 1]);
        }
        return item;
    }
}
