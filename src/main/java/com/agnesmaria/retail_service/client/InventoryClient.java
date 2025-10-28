package com.agnesmaria.retail_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryClient {

    @Value("${inventory.api.url}")
    private String inventoryApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void reduceStock(String sku, Long warehouseId, int quantity, String reference) {
        try {
            String url = inventoryApiUrl + "/api/inventory/reduce-stock";

            Map<String, Object> payload = Map.of(
                    "productSku", sku,
                    "warehouseId", warehouseId,
                    "quantity", quantity,
                    "adjustmentReason", "Retail sale transaction",
                    "movementType", "OUT",
                    "referenceNumber", reference
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            log.info("✅ Inventory updated for SKU {} ({} pcs OUT)", sku, quantity);

        } catch (Exception e) {
            log.error("❌ Failed to reduce stock for SKU {}: {}", sku, e.getMessage());
        }
    }
}
