package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.dto.RetailStockAdjustRequest;
import com.agnesmaria.retail_service.dto.RetailStockResponse;
import com.agnesmaria.retail_service.dto.RetailStockSetRequest;
import com.agnesmaria.retail_service.dto.RestockRequest;
import com.agnesmaria.retail_service.model.RetailStock;
import com.agnesmaria.retail_service.service.RetailStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> requestRestock(@Valid @RequestBody RestockRequest request) {
        stockService.requestRestock(request);
        return ResponseEntity.ok("Restock requested successfully");
    }
}
