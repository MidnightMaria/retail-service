package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.dto.RetailWarehouseRequest;
import com.agnesmaria.retail_service.model.RetailWarehouse;
import com.agnesmaria.retail_service.service.RetailWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/retail/warehouses")
@Tag(name = "Retail Warehouses", description = "Manage retail store warehouses")
public class RetailWarehouseController {

    private final RetailWarehouseService service;

    @GetMapping
    @Operation(summary = "List all retail warehouses")
    public ResponseEntity<List<RetailWarehouse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get retail warehouse by ID")
    public ResponseEntity<RetailWarehouse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create retail warehouse")
    public ResponseEntity<RetailWarehouse> create(@Valid @RequestBody RetailWarehouseRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update retail warehouse")
    public ResponseEntity<RetailWarehouse> update(@PathVariable Long id,
                                                  @Valid @RequestBody RetailWarehouseRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete retail warehouse")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
