package com.agnesmaria.retail_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryClient {

    // 🧩 Inject URL Inventory Service dari application.properties
    @Value("${inventory.api.url}")
    private String inventoryApiUrl;

    // 🧰 RestTemplate digunakan untuk komunikasi antar service
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 🔄 Transfer stock dari warehouse pusat ke retail (atau antar warehouse)
     */
    public void transferStock(Long fromWarehouseId, Long toWarehouseId, String sku, int quantity, String reference) {
        try {
            String url = inventoryApiUrl + "/api/inventory/transfer-stock";

            Map<String, Object> payload = Map.of(
                    "fromWarehouseId", fromWarehouseId,
                    "toWarehouseId", toWarehouseId,
                    "productSku", sku,
                    "quantity", quantity,
                    "reference", reference
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Requested transfer of {} units of {} from {} to {}", quantity, sku, fromWarehouseId, toWarehouseId);
            } else {
                log.warn("⚠️ Inventory Service responded with non-OK status: {}", response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory Service returned non-OK status");
            }

        } catch (Exception e) {
            log.error("❌ Failed to transfer stock: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory transfer failed: " + e.getMessage());
        }
    }
}
