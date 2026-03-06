package com.agnesmaria.retail_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

    @Value("${inventory.api.url}")
    private String inventoryApiUrl;

    private final RestTemplate restTemplate;

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

            log.info("Sending transfer request to Inventory Service: {}", payload);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Transfer success: {} units of {} ({} → {})", quantity, sku, fromWarehouseId, toWarehouseId);
            } else {
                log.warn("Inventory Service returned non-OK status: {}", response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory Service error");
            }

        } catch (Exception e) {
            log.error("Inventory transfer failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Inventory Service unreachable: " + e.getMessage());
        }
    }
}
