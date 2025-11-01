package com.agnesmaria.retail_service.controller;

import com.agnesmaria.retail_service.dto.ProductRequest;
import com.agnesmaria.retail_service.model.Product;
import com.agnesmaria.retail_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Manage retail product catalog")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all active products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @PostMapping
    @Operation(summary = "Create a new product in retail-service")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .active(request.getActive())
                .build();

        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updated
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
