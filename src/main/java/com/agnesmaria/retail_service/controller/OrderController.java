package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.dto.OrderRequestDTO;
import com.agnesmaria.retail_service.dto.OrderResponseDTO;
import com.agnesmaria.retail_service.dto.OrderExportDTO;
import com.agnesmaria.retail_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Manage retail sales orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    //Export harus di atas getById
    @GetMapping("/export")
    @Operation(summary = "Export all sales orders for analytics")
    public ResponseEntity<List<OrderExportDTO>> exportOrders() {
        return ResponseEntity.ok(orderService.exportAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create new order and reduce stock in retail warehouse")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody OrderRequestDTO request,
            @RequestParam Long warehouseId
    ) {
        return ResponseEntity.ok(orderService.createOrderFromDTO(request, warehouseId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order by ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
