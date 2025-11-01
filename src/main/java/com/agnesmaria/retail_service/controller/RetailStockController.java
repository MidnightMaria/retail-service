package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.dto.*;
import com.agnesmaria.retail_service.model.RetailStock;
import com.agnesmaria.retail_service.service.RetailStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retail/warehouses/{warehouseId}/stock")
@Tag(name = "Retail Stock", description = "Manage product stock per retail warehouse")
public class RetailStockController {

    private final RetailStockService stockService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get stock for product in a retail warehouse")
    public ResponseEntity<RetailStockResponse> get(@PathVariable Long warehouseId, @PathVariable Long productId) {
        var stock = stockService.get(warehouseId, productId);
        return ResponseEntity.ok(
            RetailStockResponse.builder()
                .id(stock.getId())
                .warehouseId(stock.getWarehouse().getId())
                .productId(stock.getProduct().getId())
                .productSku(stock.getProduct().getSku())
                .productName(stock.getProduct().getName())
                .quantity(stock.getQuantity())
                .build()
        );
    }

    @PostMapping
    @Operation(summary = "Set/replace quantity (create if not exists)")
    public ResponseEntity<RetailStock> setQuantity(@PathVariable Long warehouseId,
                                                   @Valid @RequestBody RetailStockSetRequest req) {
        return ResponseEntity.ok(stockService.setQuantity(warehouseId, req));
    }

    @PostMapping("/increase")
    @Operation(summary = "Increase quantity")
    public ResponseEntity<RetailStock> increase(@PathVariable Long warehouseId,
                                                @Valid @RequestBody RetailStockAdjustRequest req) {
        return ResponseEntity.ok(stockService.increase(warehouseId, req));
    }

    @PostMapping("/decrease")
    @Operation(summary = "Decrease quantity (will fail if insufficient)")
    public ResponseEntity<RetailStock> decrease(@PathVariable Long warehouseId,
                                                @Valid @RequestBody RetailStockAdjustRequest req) {
        return ResponseEntity.ok(stockService.decrease(warehouseId, req));
    }

    @PostMapping("/restock")
    @Operation(summary = "Request restock from central warehouse")
    public ResponseEntity<Map<String, Object>> requestRestock(
            @PathVariable Long warehouseId,
            @Valid @RequestBody RestockRequest request) {

        // Gunakan warehouseId dari path sebagai tujuan retail
        request.setToWarehouseId(warehouseId);

        // Panggil service untuk proses restock
        stockService.requestRestock(request);

        // Kembalikan respons yang lebih informatif
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Restock requested successfully",
                "toWarehouseId", warehouseId,
                "productSku", request.getProductSku(),
                "quantity", request.getQuantity()
        ));
    }
}
